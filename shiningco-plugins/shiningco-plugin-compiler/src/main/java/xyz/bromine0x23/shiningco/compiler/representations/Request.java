package xyz.bromine0x23.shiningco.compiler.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Request {

	@JsonProperty("lang")
	private String lang;

	@JsonProperty("compiler")
	private String compiler;

	@JsonProperty("source")
	private String source;

	@JsonProperty("options")
	@Builder.Default
	private Options options = new Options();

	@JsonProperty("allowStoreCodeDebug")
	@Builder.Default
	private boolean allowStoreCodeDebug = true;

	@Data
	public static class Options {

		@JsonProperty("userArguments")
		private String userArguments = "";

		@JsonProperty("compilerOptions")
		private CompilerOptions compilerOptions = new CompilerOptions();

		@JsonProperty("filters")
		private Filters filters = new Filters();

		@JsonProperty("libraries")
		private List<Library> libraries = new ArrayList<>();

		@JsonProperty("tools")
		private List<Tool> tools = new ArrayList<>();

		@Data
		public static class CompilerOptions {
			@JsonProperty("produceCfg")
			private boolean produceCfg = false;

			@JsonProperty("produceGccDump")
			private Map<Object, Object> produceGccDump = Collections.emptyMap();
		}

		@Data
		public static class Filters {
			@JsonProperty("binary")
			private boolean binary = false;
			@JsonProperty("execute")
			private boolean execute = true;
			@JsonProperty("intel")
			private boolean intel = false;
			@JsonProperty("demangle")
			private boolean demangle = false;

			@JsonProperty("labels")
			private boolean labels = true;
			@JsonProperty("libraryCode")
			private boolean libraryCode = true;
			@JsonProperty("directives")
			private boolean directives = true;
			@JsonProperty("commentOnly")
			private boolean commentOnly = true;
			@JsonProperty("trim")
			private boolean trim = true;
		}

		@Data
		public static class Library {
			@JsonProperty("id")
			private String id;

			@JsonProperty("version")
			private String version;
		}

		@Data
		public static class Tool {
			@JsonProperty("id")
			private String id;

			@JsonProperty("args")
			private String args;

			@JsonProperty("stdin")
			private String stdin;
		}

	}

}
