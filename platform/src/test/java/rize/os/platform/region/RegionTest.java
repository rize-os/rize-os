package rize.os.platform.region;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegionTest
{
    @Test
    void shouldBeValidRegion()
    {
        Region region = Region.builder()
                .id(UUID.randomUUID())
                .name("test-region-1")
                .displayName("Test Region")
                .build();

        assertThat(region.validate()).isEmpty();
    }

    @Test
    void shouldHaveInvalidId()
    {
        Region region = Region.builder()
                .name("test-region-1")
                .displayName("Test Region")
                .build();
        assertThat(region.validate()).hasSize(1);
    }

    @Test
    void shouldHaveInvalidRegionName()
    {
        Region region = Region.builder()
                .id(UUID.randomUUID())
                .name("test-region-")
                .displayName("Test Region")
                .build();
        assertThat(region.validate()).hasSize(1);

        region.setName("-test-region");
        assertThat(region.validate()).hasSize(1);

        region.setName("Test Region");
        assertThat(region.validate()).hasSize(1);

        region.setName("test_region");
        assertThat(region.validate()).hasSize(1);

        region.setName("test region");
        assertThat(region.validate()).hasSize(1);
    }

    @Test
    void shouldHaveInvalidDisplayName()
    {
        Region region = Region.builder()
                .id(UUID.randomUUID())
                .name("test-region-1")
                .build();
        assertThat(region.validate()).hasSize(1);

        region.setDisplayName("");
        assertThat(region.validate()).hasSize(1);
    }

    @Test
    void shouldBeEqual()
    {
        Region region1 = Region.builder()
                .id(UUID.randomUUID())
                .name("test-region-1")
                .displayName("Test Region")
                .build();
        Region region2 = Region.builder()
                .id(region1.getId())
                .name("test-region-1")
                .displayName("Test Region")
                .build();

        assertThat(region1).isEqualTo(region2);
        assertThat(region1.hashCode()).isEqualTo(region2.hashCode());
        assertThat(region1.toString()).isEqualTo(region2.toString());
    }
}