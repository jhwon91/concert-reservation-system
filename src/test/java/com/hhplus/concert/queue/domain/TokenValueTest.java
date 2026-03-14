package com.hhplus.concert.queue.domain;

import com.hhplus.concert.queue.domain.vo.TokenValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenValueTest {

    @Test
    @DisplayName("generate()로 생성된 토큰 값은 비어있지 않다")
    void generate_not_blank() {
        // when
        TokenValue token = TokenValue.generate();

        // then
        assertThat(token.getValue()).isNotBlank();
    }

    @Test
    @DisplayName("동일한 generate() 호출은 서로 다른 토큰을 생성한다")
    void generate_unique() {
        // when
        TokenValue token1 = TokenValue.generate();
        TokenValue token2 = TokenValue.generate();

        // then
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("같은 값으로 생성한 토큰은 동등하다")
    void of_same_value_equals() {
        // given
        String value = "same-token-value";

        // when
        TokenValue token1 = TokenValue.of(value);
        TokenValue token2 = TokenValue.of(value);

        // then
        assertThat(token1).isEqualTo(token2);
    }

    @Test
    @DisplayName("null 값으로 생성 시 예외가 발생한다")
    void of_null_throws() {
        assertThatThrownBy(() -> TokenValue.of(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("빈 값으로 생성 시 예외가 발생한다")
    void of_blank_throws() {
        assertThatThrownBy(() -> TokenValue.of("  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
