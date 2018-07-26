package info.signaltrade.base.model;

/**
 * Abstract class for models
 */
public abstract class AbstractModel
{
    /**
     * Id
     */
    private int id;

    /**
     * Constructor by default
     */
    public AbstractModel() { }

    /**
     * Set Id
     *
     * @param id - new Id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get Id
     *
     * @return - id
     */
    public int getId()
    {
        return id;
    }


}
