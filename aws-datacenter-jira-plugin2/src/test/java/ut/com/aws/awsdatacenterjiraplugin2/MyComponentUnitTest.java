package ut.com.aws.awsdatacenterjiraplugin2;

import org.junit.Test;
import com.aws.awsdatacenterjiraplugin2.api.MyPluginComponent;
import com.aws.awsdatacenterjiraplugin2.impl.MyPluginComponentImpl;

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