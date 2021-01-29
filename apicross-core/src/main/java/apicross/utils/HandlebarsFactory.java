package apicross.utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HandlebarsFactory {
    public static Handlebars setupHandlebars(String... templateClassPathItems) {
        List<TemplateLoader> loaders = new ArrayList<>();
        for (String templateClassPathItem : templateClassPathItems) {
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix(templateClassPathItem);
            loader.setSuffix(".hbs");
            loaders.add(loader);
        }
        Handlebars handlebars = loaders.size() > 1 ? new Handlebars(new CompositeTemplateLoader(loaders.toArray(new TemplateLoader[0]))) : new Handlebars(loaders.get(0));

        handlebars.registerHelper("capitalizeFirst", StringHelpers.capitalizeFirst);
        handlebars.registerHelper("join", StringHelpers.join);
        handlebars.registerHelper("lower", StringHelpers.lower);
        handlebars.registerHelper("eq", ConditionalHelpers.eq);
        handlebars.registerHelper("or", ConditionalHelpers.or);
        handlebars.registerHelper("javaBackSlashEscape",
                (value, options) -> value.toString().replace("\\", "\\\\"));

        return handlebars;
    }
}
