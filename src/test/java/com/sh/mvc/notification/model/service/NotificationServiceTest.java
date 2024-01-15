package com.sh.mvc.notification.model.service;

import com.sh.mvc.common.HelloMvcUtils;
import com.sh.mvc.notification.model.entity.Notification;
import com.sh.mvc.notification.model.entity.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        this.notificationService = new NotificationService();
    }


    @DisplayName("알림 내용에 링크를 추가한다. (Context Path는 제외하고 테스트) ")
    @ParameterizedTest
    @CsvSource({
        "Helloworld,/board/boardDetail?id=123,<a href=\"/board/boardDetail?id=123\" class=\"hover:underline text-blue-500\">Helloworld</a>",
        "honggd,/member/memberView?id=honggd,<a href=\"/member/memberView?id=honggd\" class=\"hover:underline text-blue-500\">honggd</a>"
    })
    void test1(String content, String url, String expected) {
        // given
        assertThat(content).isNotNull();
        assertThat(url).isNotNull();
        assertThat(expected).isNotNull();
        // when
        String actual = HelloMvcUtils.getBoardCommentNotification(content, url);

        // then
        assertThat(actual).isEqualTo(expected);
    }
    
    @Disabled
    @DisplayName("한행의 알림 데이터를 기록한다.")
    @ParameterizedTest
    @CsvSource({
        "qwerty,NEW_BOARD_COMMENT,<a href=\"/mvc/member/memberView?id=honggd\" class=\"hover:underline text-blue-500\">honggd</a>님이 <a href=\"/mvc/board/boardDetail?id=123\" class=\"hover:underline text-blue-500\">Helloworld</a> 게시글에 댓글을 작성했습니다.",
        "qwerty,NEW_BOARD_COMMENT,<a href=\"/mvc/member/memberView?id=honggd\" class=\"hover:underline text-blue-500\">honggd</a>님이 나를 팔로우했습니다."
    })
    void test2(String memberId, Type type, String content) {
        // given
        assertThat(memberId).isNotNull();
        assertThat(type).isNotNull();
        assertThat(content).isNotNull();
        // when
        Notification noti = new Notification();
        noti.setMemberId(memberId);
        noti.setType(type);
        noti.setContent(content);
        int result = notificationService.insertNotification(noti);
        // then
        assertThat(result).isGreaterThan(0);
    }

    @DisplayName("특정회원의 확인안한 알림내역을 조회한다.")
    @ParameterizedTest
    @ValueSource(strings = {"qwerty", "abcde"})
    void test3(String memberId) {
        // given
        assertThat(memberId).isNotNull();
        // when
        List<Notification> notifications = notificationService.findByMemberId(memberId);
        System.out.println(notifications);
        // then
        assertThat(notifications)
                .isNotNull()
                .allSatisfy((noti) -> {
                   assertThat(noti.getId()).isNotNull().isNotZero();
                   assertThat(noti.getMemberId()).isEqualTo(memberId);
                   assertThat(noti.getType()).isNotNull();
                   assertThat(noti.getContent()).isNotNull();
                   assertThat(noti.isChecked()).isFalse(); // 확인 안한 알림
                   assertThat(noti.getRegDate()).isNotNull();
                });
    }
}