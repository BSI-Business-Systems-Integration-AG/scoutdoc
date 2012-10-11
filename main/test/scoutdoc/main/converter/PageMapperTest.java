package scoutdoc.main.converter;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.TU;

public class PageMapperTest {

  @Test
  public void testMapPageNameToHref() {
    TU.init();

    ConversionItem c1 = new ConversionItem();
    c1.inputPage = TU.PAGE_1;
    c1.outputFileName = "index.html";

    ConversionItem c2 = new ConversionItem();
    c2.inputPage = TU.PAGE_2;
    c2.outputFileName = "index2.html";

    PageMapper pageMapper = new PageMapper(Arrays.asList(c1, c2));

    Assert.assertEquals("index.html", pageMapper.mapPageNameToHref("Test_Page1"));
    Assert.assertEquals("index2.html", pageMapper.mapPageNameToHref("Test_Page2"));
    Assert.assertEquals("index2.html", pageMapper.mapPageNameToHref("Test_Red2"));
    Assert.assertEquals("index2.html", pageMapper.mapPageNameToHref("Test_Red1"));
    Assert.assertEquals(null, pageMapper.mapPageNameToHref("Test_Page3"));
    Assert.assertEquals(null, pageMapper.mapPageNameToHref("Test_Red3"));
    Assert.assertEquals(null, pageMapper.mapPageNameToHref("Test_RedSelf"));
  }

}
