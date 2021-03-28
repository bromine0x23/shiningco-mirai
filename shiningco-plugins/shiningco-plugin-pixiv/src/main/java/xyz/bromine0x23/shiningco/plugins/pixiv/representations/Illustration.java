package xyz.bromine0x23.shiningco.plugins.pixiv.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Illustration {

	@JsonProperty("id")
	private long id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("type")
	private String type;

	@JsonProperty("caption")
	private String caption;

	@JsonProperty("image_urls")
	private ImageUrls imageUrls = new ImageUrls();

	@JsonProperty("user")
	private User user = new User();

	@JsonProperty("sanity_level")
	private int sanityLevel;

	@JsonProperty("create_date")
	private OffsetDateTime createDate;

	@JsonProperty("tags")
	private List<Tag> tags = new ArrayList<>();

	@JsonProperty("meta_single_page")
	private MetaSinglePage metaSinglePage = new MetaSinglePage();

	@JsonProperty("meta_pages")
	private List<MetaPage> metaPages = new ArrayList<>();

}
