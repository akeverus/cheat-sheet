package com.cheatsheet.quiz.service.imports;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashingServiceTest {

    private final HashingService service = new HashingService();

    @Test
    void sha256ReturnsKnownHashForEmptyString() {
        // SHA-256("") = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
        String hash = service.sha256("");
        assertThat(hash).isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }

    @Test
    void sha256ReturnsKnownHashForAsciiString() {
        // SHA-256("abc") = ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
        String hash = service.sha256("abc");
        assertThat(hash).isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }

    @Test
    void sha256ReturnsHashOfFixedLength() {
        String hash = service.sha256("любая строка с unicode символами");
        assertThat(hash).hasSize(64); // 256 бит = 32 байта = 64 hex-символа
        assertThat(hash).matches("[0-9a-f]{64}");
    }

    @Test
    void sha256IsDeterministic() {
        String first = service.sha256("повтори меня");
        String second = service.sha256("повтори меня");
        assertThat(first).isEqualTo(second);
    }

    @Test
    void sha256DifferentForDifferentInputs() {
        String hashA = service.sha256("a");
        String hashB = service.sha256("b");
        assertThat(hashA).isNotEqualTo(hashB);
    }
}
