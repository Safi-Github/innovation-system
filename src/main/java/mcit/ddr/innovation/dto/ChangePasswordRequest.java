package mcit.ddr.innovation.dto;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword,
        String confirmNewPassword
) {}
