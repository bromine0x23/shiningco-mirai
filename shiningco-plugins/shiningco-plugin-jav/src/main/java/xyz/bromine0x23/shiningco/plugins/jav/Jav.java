package xyz.bromine0x23.shiningco.plugins.jav;

import lombok.Data;

import java.util.List;

@Data
public class Jav {

	String title;

	String manufacturer;

	List<String> actors;

	List<String> tags;

	String coverUrl;

	List<String> magnets;

}
