package checkspec.examples.example3;

import java.util.regex.Pattern;

import lombok.Value;

@Value
public class RegexPackageSpecification {

	private Pattern packagePattern;
}