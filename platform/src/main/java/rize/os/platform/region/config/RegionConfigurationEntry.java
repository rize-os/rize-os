package rize.os.platform.region.config;

public enum RegionConfigurationEntry
{
    ENABLED;

    String getValueRegex()
    {
        return "^(true|false)$";
    }

    String getDefaultValue()
    {
        return "false";
    }

    @Override
    public String toString()
    {
        return this.name().toLowerCase();
    }
}
