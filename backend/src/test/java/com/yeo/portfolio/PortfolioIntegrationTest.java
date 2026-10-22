package com.yeo.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PortfolioIntegrationTest {
    @Autowired PortfolioController controller;
    @Test void seededPortfolioConnectsProjectsAndContents() {
        assertThat(controller.profile().instagramUrl()).isEqualTo("https://www.instagram.com/1._.yeo/");
        assertThat(controller.profile().email()).isEqualTo("wjddudnjs03@naver.com");
        assertThat(controller.profile().name()).isEqualTo("정여원");
        assertThat(controller.profile().sample()).isFalse();
        assertThat(controller.profile().information().path("channel").path("metrics").get(2).path("label").asText()).isEqualTo("최근 30일 채널 전체 조회");
        assertThat(controller.projects()).hasSize(3);
        assertThat(controller.contents()).hasSize(15);
        var reels = controller.contents().stream().filter(content -> content.type() == Content.Type.REEL).toList();
        assertThat(reels).extracting(PortfolioController.ContentView::title).containsExactly("색조", "스킨케어", "프래그런스", "기타", "클렌징", "이너케어");
        assertThat(reels).extracting(PortfolioController.ContentView::instagramUrl).containsExactly(
            "https://www.instagram.com/reel/DUHroHgAXRx/", "https://www.instagram.com/reel/DYO7_kXBxSf/",
            "https://www.instagram.com/reel/Dck4yGjh0DG/", "https://www.instagram.com/reel/DckhpgKhYyO/",
            "https://www.instagram.com/reel/Dc-0qCKh5Uo/", "https://www.instagram.com/reel/DWsXPeLAU8Y/");
        assertThat(reels).extracting(content -> java.util.stream.IntStream.range(0, 5).map(index -> content.insights().path("reactions").get(index).asInt()).sum()).containsExactly(77, 51, 153, 674, 125, 65);
        assertThat(reels.get(0).insights().path("views").asInt()).isEqualTo(23160);
        assertThat(reels.get(0).insights().path("follows").asInt()).isZero();
        assertThat(reels.get(1).insights().path("reach").isNull()).isTrue();
        assertThat(reels.get(2).insights().path("viewers").asInt()).isEqualTo(3442);
        assertThat(reels.get(3).insights().path("reactions").get(2).asInt() + reels.get(3).insights().path("reactions").get(3).asInt()).isEqualTo(307);
        for (var project : controller.projects()) {
            var detail = controller.project(project.id());
            assertThat(detail.sample()).isTrue();
            assertThat(detail.purpose()).isEmpty();
            assertThat(detail.message()).isEmpty();
            assertThat(detail.intention()).isEmpty();
            assertThat(detail.role()).isEmpty();
            assertThat(detail.learning()).isEmpty();
            assertThat(detail.contents()).hasSize(5).allSatisfy(content -> assertThat(content.projectId()).isEqualTo(project.id()));
            assertThat(detail.contents()).extracting(PortfolioController.ContentView::type).containsExactlyInAnyOrder(Content.Type.FEED, Content.Type.FEED, Content.Type.FEED, Content.Type.REEL, Content.Type.REEL);
            assertThat(detail.contents()).allSatisfy(content -> {
                assertThat(content.category()).isNull();
                if (content.type() == Content.Type.FEED) {
                    assertThat(content.brand()).isEmpty();
                    assertThat(content.productName()).isEmpty();
                }
                assertThat(content.videoUrl()).isEmpty();
                if (content.type() == Content.Type.FEED) assertThat(content.externalUrl()).isEmpty();
            });
        }
    }
    @Test void unknownProjectReturnsNotFound() {
        assertThatThrownBy(() -> controller.project(9999L)).isInstanceOfSatisfying(ResponseStatusException.class, exception -> assertThat(exception.getStatusCode().value()).isEqualTo(404));
    }
}
