package spark.template.velocity;

import com.googlecode.htmlcompressor.compressor.Compressor;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import kotlin.Pair;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by alewis on 17/05/2017.
 */
public class VelocityIMTemplateEngine {
    private final VelocityEngine velocityEngine;
    private StringWriter writer = new StringWriter();

    /**
     * Constructor
     */
    public VelocityIMTemplateEngine() {
        Properties properties = new Properties();
        properties.setProperty("resource.loader", "string");
        properties.setProperty("description", "Velocity StringResource loader");
        properties.setProperty(
                "class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        velocityEngine = new org.apache.velocity.app.VelocityEngine(properties);
    }

    /**
     * Constructor
     *
     * @param velocityEngine The velocity engine, must not be null.
     */
    public VelocityIMTemplateEngine(VelocityEngine velocityEngine) {
        if (velocityEngine == null) {
            throw new IllegalArgumentException("velocityEngine must not be null");
        }
        this.velocityEngine = velocityEngine;
    }

    public void insertTemplateAsString(String templateTitle, String templateContent) {
        velocityEngine.init();
        StringResourceRepository stringResourceRepository = StringResourceLoader.getRepository();
        stringResourceRepository.putStringResource(templateTitle, templateContent);
    }

    public void insertContextsToIMTemplate(String templateTitle, HashMap<String, Object> keyAndValues) {
        VelocityContext velocityContext = new VelocityContext();
        for (Map.Entry<String, Object> entry : keyAndValues.entrySet()) {
            velocityContext.put(entry.getKey(), entry.getValue());
        }
        Template template = velocityEngine.getTemplate(templateTitle);
        template.merge(velocityContext, writer);
    }

    public void insertContextToIMTemplate(String templateTitle, Pair<String, Object> keyAndValue) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put(keyAndValue.getFirst(), keyAndValue.getSecond());
        Template template = velocityEngine.getTemplate(templateTitle);
        template.merge(velocityContext, writer);
    }

    public void insertContextsToIMTemplate(String templateTitle, List<Pair<String, Object>> keyAndValues) {
        VelocityContext velocityContext = new VelocityContext();
        keyAndValues.forEach(pair -> velocityContext.put(pair.getFirst(), pair.getSecond()));
        Template template = velocityEngine.getTemplate(templateTitle);
        template.merge(velocityContext, writer);
    }

    public void clearIMTemplate() { writer.flush(); }
    public String getMergedIMTemplate() {
        Compressor htmlCompressor = new HtmlCompressor();
        return htmlCompressor.compress(writer.toString());
    }
}
