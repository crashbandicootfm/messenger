package dev.crashbandicootfm.messenger.service.api.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyTwoFactorRequest {
    private int code;

    // getter and setter
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}