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
import java.util.*;

/**
 * Created by alewis on 17/05/2017.
 */
public class VelocityIMTemplateEngine {

    private final VelocityEngine velocityEngine;
    private HashMap<String, VelocityContext> templatesAndContexts = new HashMap<>();

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
        velocityEngine.init();
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
        this.velocityEngine.init();
    }

    public void insertTemplateAsString(String templateTitle, String templateContent) {
        StringResourceRepository stringResourceRepository = StringResourceLoader.getRepository();
        stringResourceRepository.putStringResource(templateTitle, templateContent);
        templatesAndContexts.put(templateTitle, new VelocityContext());
    }

    public void insertIntoContext(String templateTitle, HashMap<String, Object> keyAndValues) {
        VelocityContext velocityContext = templatesAndContexts.get(templateTitle);
        if (velocityContext != null) {
            for (Map.Entry<String, Object> entry : keyAndValues.entrySet()) {
                velocityContext.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public void insertIntoContext(String templateTitle, List<Pair<String, Object>> keyAndValues) {
        VelocityContext velocityContext = templatesAndContexts.get(templateTitle);
        if (velocityContext != null) {
            keyAndValues.forEach(pair -> velocityContext.put(pair.getFirst(), pair.getSecond()));
        }
    }

    public void insertIntoContext(String templateTitle, Pair<String, Object> keyAndValue) {
        VelocityContext velocityContext = templatesAndContexts.get(templateTitle);
        velocityContext.put(keyAndValue.getFirst(), keyAndValue.getSecond());
    }

    public String render(String templateTitle) {
        StringWriter writer = new StringWriter();
        Template template = velocityEngine.getTemplate(templateTitle, "UTF-8");
        VelocityContext velocityContext = templatesAndContexts.get(templateTitle);
        template.merge(velocityContext, writer);
        Compressor htmlCompressor = new HtmlCompressor();
        return htmlCompressor.compress(writer.toString());
    }

    public void flush(String templateTitle) {
        templatesAndContexts.remove(templateTitle);
        StringResourceRepository stringResourceRepository = StringResourceLoader.getRepository();
        if (velocityEngine.resourceExists(templateTitle)) {
            stringResourceRepository.removeStringResource(templateTitle);
        }
    }
}
