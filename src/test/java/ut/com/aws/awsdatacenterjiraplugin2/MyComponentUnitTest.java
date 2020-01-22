package ut.com.aws.awsdatacenterjiraplugin2;

import com.aws.apn.migration.serverPlugin.api.MyPluginComponent;
import com.aws.apn.migration.serverPlugin.impl.MyPluginComponentImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}