package com.he180773.testreact.controller.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.he180773.testreact.dto.DepositRequestDTO;
import com.he180773.testreact.entity.*;
import com.he180773.testreact.repository.WalletRepository;
import com.he180773.testreact.repository.WalletTransactionRepository;
import com.he180773.testreact.service.UserService;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallet")
//@CrossOrigin(origins = "https://leventstreetwear.id.vn")
public class WalletController {
    private final WalletRepository walletRepository;
    private final UserService userService;
    private final WalletTransactionRepository walletTransactionRepository;
    private final RestTemplate restTemplate;

    public WalletController(WalletRepository walletRepository, UserService userService,
                            WalletTransactionRepository walletTransactionRepository,
                            RestTemplate restTemplate) {
        this.walletRepository = walletRepository;
        this.userService = userService;
        this.walletTransactionRepository = walletTransactionRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getWallet(@RequestParam Long userId) {
        System.out.println("hihi: "+userId);
        Wallet wallet = walletRepository.findByUserId(userId).orElse(null);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/handle")
    public ResponseEntity<?> createDepositRequest(@RequestBody DepositRequestDTO dto) {
        // Lưu vào DB
        System.out.println("ID: "+dto.getUserId());
        System.out.println("Created ");
        WalletTransaction walletTransaction = new WalletTransaction();
        Wallet wallet = walletRepository.findByUserId(dto.getUserId()).orElse(null);
        walletTransaction.setWalletId(wallet.getId());
        walletTransaction.setAmount(dto.getAmount());
        walletTransaction.setTransactionCode(dto.getTransactionCode());
        walletTransaction.setCreatedAt(LocalDateTime.now());
        walletTransaction.setStatus("PENDING");
        walletTransactionRepository.save(walletTransaction);
        return ResponseEntity.ok("Deposit request saved");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTransactions(@PathVariable Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId).orElse(null);
        System.out.println("wallet: "+wallet.getId());
        return ResponseEntity.ok(walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId()));
    }



    @Scheduled(fixedRate = 30000) // 1 phút
    public void checkPendingTransactions() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<WalletTransaction> pendingTransactions = walletTransactionRepository.findByStatus("PENDING");
        for (WalletTransaction t : pendingTransactions) {
            boolean matched = checkWithSepayAPI(t.getTransactionCode(), t.getAmount());
            if (matched) {
                System.out.println("matched");
                t.setStatus("DONE");
                t.setConfirmedAt(LocalDateTime.now());
                t.setTransactionCode(t.getTransactionCode());
                walletTransactionRepository.save(t);

                // Cộng tiền vào ví user nếu cần

                Optional<Wallet> walletOpt = walletRepository.findById(t.getWalletId());
                Wallet wallet = walletOpt.orElse(null);
                        wallet.setBalance(wallet.getBalance()+t.getAmount());
                walletRepository.save(wallet);
            }
            else {
                // Optional: Nếu quá 5 phút thì xóa hoặc đánh dấu hết hạn
                if (t.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(10))) {
                    walletTransactionRepository.delete(t);
                }
            }
        }
    }

    public boolean checkWithSepayAPI(String transactionCode, Integer amount) {
        String url = "https://my.sepay.vn/userapi/transactions/list";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("GSA6INY97MR4URKVRAVHR3VOQGQAL20CS1BBG5PQI4KKFFECCB9NFYT3Z27ZXXNU");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("🔎 JSON Sepay trả về: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode transactions = root.get("transactions");

                if (transactions != null && transactions.isArray()) {
                    for (JsonNode txn : transactions) {
                        String content = txn.get("transaction_content").asText();
                        String amountInStr = txn.get("amount_in").asText();

                        System.out.println("➡️ Nội dung: " + content);
                        System.out.println("➡️ Tiền vào: " + amountInStr);
                        String sanitizedTransactionCode = transactionCode.replace("_", "");

                        System.out.println("sanitizedTransactionCode: " + sanitizedTransactionCode);


                        if (content != null && content.contains(sanitizedTransactionCode)) {
                            try {
                                double parsedAmount = Double.parseDouble(amountInStr);
                                if ((int) parsedAmount == amount) {
                                    return true;
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("⚠️ Không parse được amount_in: " + amountInStr);
                            }
                        }
                    }
                } else {
                    System.out.println("⚠️ Không tìm thấy trường 'transactions' hoặc không phải mảng.");
                }
            }

        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }




}
