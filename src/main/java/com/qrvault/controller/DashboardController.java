package com.qrvault.controller;

import com.qrvault.model.QRHistory;
import com.qrvault.model.User;
import com.qrvault.repository.QRHistoryRepository;
import com.qrvault.service.QRCodeService;
import com.qrvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired private UserService userService;
    @Autowired private QRCodeService qrCodeService;
    @Autowired private QRHistoryRepository qrHistoryRepository;

    // ─── Root redirect ─────────────────────────────────────────────────────────

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    // ─── Dashboard ─────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getUser(userDetails);
        List<QRHistory> history = qrHistoryRepository.findByUserOrderByCreatedAtDesc(user);
        long totalCount = qrHistoryRepository.countByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("history", history.stream().limit(5).toList());
        model.addAttribute("totalQRs", totalCount);
        return "dashboard/index";
    }

    // ─── Generate QR (AJAX endpoint, returns JSON) ─────────────────────────────

    @PostMapping("/generate")
    @ResponseBody
    public java.util.Map<String, Object> generateQR(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String qrType,
            @RequestParam(required = false, defaultValue = "") String urlData,
            @RequestParam(required = false, defaultValue = "") String textData,
            @RequestParam(required = false, defaultValue = "") String wifiSsid,
            @RequestParam(required = false, defaultValue = "") String wifiPassword,
            @RequestParam(required = false, defaultValue = "WPA") String wifiSecurity,
            @RequestParam(required = false, defaultValue = "") String contactFirst,
            @RequestParam(required = false, defaultValue = "") String contactLast,
            @RequestParam(required = false, defaultValue = "") String contactPhone,
            @RequestParam(required = false, defaultValue = "") String contactEmail,
            @RequestParam(defaultValue = "200") int size,
            @RequestParam(defaultValue = "M") String ecLevel,
            @RequestParam(defaultValue = "#000000") String fgColor,
            @RequestParam(defaultValue = "#ffffff") String bgColor
    ) {
        try {
            // Build the data string based on QR type
            String data = switch (qrType.toUpperCase()) {
                case "URL"     -> urlData.isBlank() ? "https://example.com" : urlData;
                case "TEXT"    -> textData.isBlank() ? "Hello World" : textData;
                case "WIFI"    -> qrCodeService.buildWifiData(wifiSsid, wifiPassword, wifiSecurity);
                case "CONTACT" -> qrCodeService.buildContactData(contactFirst, contactLast, contactPhone, contactEmail);
                default        -> urlData;
            };

            String base64 = qrCodeService.generateQRCodeBase64(data, size, ecLevel, fgColor, bgColor);

            // Save to history
            User user = getUser(userDetails);
            QRHistory entry = new QRHistory(user, qrType.toUpperCase(), data);
            qrHistoryRepository.save(entry);

            return java.util.Map.of("success", true, "image", base64, "data", data);
        } catch (Exception e) {
            return java.util.Map.of("success", false, "error", e.getMessage());
        }
    }

    // ─── Download endpoint ─────────────────────────────────────────────────────

    @PostMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String base64Image) {
        try {
            String raw = base64Image.replace("data:image/png;base64,", "");
            byte[] bytes = Base64.getDecoder().decode(raw);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qrcode.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ─── History page ──────────────────────────────────────────────────────────

    @GetMapping("/history")
    public String history(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getUser(userDetails);
        List<QRHistory> history = qrHistoryRepository.findByUserOrderByCreatedAtDesc(user);
        model.addAttribute("user", user);
        model.addAttribute("history", history);
        return "dashboard/history";
    }

    // ─── Helper ────────────────────────────────────────────────────────────────

    private User getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
