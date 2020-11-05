package hu.icellmobilsoft.roaster.api.dto;

/**
 * base builder
 * 
 * @author czenczl
 * @version 0.2.0
 */
public abstract class BaseBuilder<T> {

    private T dto;

    /**
     * create empty dto
     * 
     * @return empty dto
     */
    public abstract T createEmpty();

    public T getDto() {
        return dto;
    }

    public void setDto(T dto) {
        this.dto = dto;
    }

}
