package checkspec.specification;

public interface Specification<RawType> {

	public String getName();

	public RawType getRawElement();
}
