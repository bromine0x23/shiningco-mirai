package xyz.bromine0x23.shiningco.plugins.bangumi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BangumiSubject {

	/**
	 * 条目 ID
	 */
	@JsonProperty("id")
	private Integer id;

	/**
	 * 条目地址
	 */
	@JsonProperty("url")
	private String url;

	/**
	 * 条目类型
	 */
	@JsonProperty("type")
	private BangumiSubjectType type;

	/**
	 * 条目名称
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * 条目中文名称
	 */
	@JsonProperty("name_cn")
	private String nameCN;

	/**
	 * 剧情简介
	 */
	@JsonProperty("summary")
	private String summary;

	/**
	 * 放送开始日期
	 */
	@JsonProperty("air_date")
	private String airDate;

	/**
	 * 放送星期
	 */
	@JsonProperty("air_weekday")
	private String airWeekday;

	/**
	 * 放送星期
	 */
	@JsonProperty("images")
	private Images images = new Images();

	/**
	 * 排名
	 */
	@JsonProperty("rating")
	private Rating rating = new Rating();

	/**
	 * 排名
	 */
	@JsonProperty("rank")
	private Integer rank;

	@Data
	public static class Rating {

		@JsonProperty("total")
		private Integer total;

		@JsonProperty("score")
		private Double score;

	}

	@Data
	public static class Images {

		@JsonProperty("large")
		private String large;

		@JsonProperty("common")
		private String common;

		@JsonProperty("medium")
		private String medium;

		@JsonProperty("small")
		private String small;

		@JsonProperty("grid")
		private String grid;
	}
}
