package com.cts.trialledger.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lightweight in-memory store for one-time passwords used during the
 * password-reset flow. Keys are normalized (lower-cased) email addresses,
 * values are 6-digit OTPs that expire 5 minutes after issue.
 *
 * <p>This is intentionally process-local — for a multi-instance deployment
 * we'd swap to Redis with the same surface. Concurrency is handled with
 * {@link ConcurrentHashMap} plus an {@link AtomicInteger} attempts counter,
 * so the {@link #verify(String, String)} method can safely lock out brute
 * forces while staying lock-free.</p>
 */
@Slf4j
@Component
public class OtpStore {

    public static final Duration OTP_TTL = Duration.ofMinutes(5);
    public static final int MAX_ATTEMPTS = 5;

    private final SecureRandom rng = new SecureRandom();
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    /** Generate (or replace) an OTP for the given email and return it. */
    public String issue(String email) {
        String otp = String.format(Locale.ROOT, "%06d", rng.nextInt(1_000_000));
        Entry entry = new Entry(otp, Instant.now().plus(OTP_TTL));
        store.put(key(email), entry);

        // Console-log the OTP per product decision — replaces the bypass
        // of "directly changing the password without verification".
        log.info("================= PASSWORD-RESET OTP =================");
        log.info("  email : {}", email);
        log.info("  otp   : {}   (valid for {} minutes)", otp, OTP_TTL.toMinutes());
        log.info("======================================================");
        return otp;
    }

    /**
     * Verify the supplied OTP for an email. Returns {@code true} only when
     * the OTP matches AND is unexpired. On success the entry is consumed
     * (single-use). On expiry / mismatch the attempts counter increments;
     * if it exceeds {@link #MAX_ATTEMPTS} the entry is wiped so the user
     * must request a new OTP.
     */
    public VerifyResult verify(String email, String otp) {
        Entry e = store.get(key(email));
        if (e == null) return VerifyResult.NOT_REQUESTED;
        if (Instant.now().isAfter(e.expiresAt)) {
            store.remove(key(email));
            return VerifyResult.EXPIRED;
        }
        if (!e.otp.equals(otp)) {
            int attempts = e.attempts.incrementAndGet();
            if (attempts >= MAX_ATTEMPTS) {
                store.remove(key(email));
                return VerifyResult.TOO_MANY_ATTEMPTS;
            }
            return VerifyResult.MISMATCH;
        }
        // single-use: consume the entry
        store.remove(key(email));
        return VerifyResult.OK;
    }

    public void invalidate(String email) {
        store.remove(key(email));
    }

    private String key(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    public enum VerifyResult { OK, MISMATCH, EXPIRED, NOT_REQUESTED, TOO_MANY_ATTEMPTS }

    private static final class Entry {
        final String otp;
        final Instant expiresAt;
        final AtomicInteger attempts = new AtomicInteger(0);
        Entry(String otp, Instant expiresAt) { this.otp = otp; this.expiresAt = expiresAt; }
    }
}
