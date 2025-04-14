package mcit.ddr.innovation.dto;

public record ResetPasswordRequest(String newPassword, String confirmNewPassword, String otpCode) {
}
