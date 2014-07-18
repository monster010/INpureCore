package info.inpureprojects.core.Scripting;

import com.google.common.eventbus.EventBus;
import info.inpureprojects.core.API.Events.EventExposeObjects;
import info.inpureprojects.core.API.Events.EventSetScriptFolder;
import info.inpureprojects.core.Scripting.Objects.ExposedObject;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by den on 7/16/2014.
 */
public class ScriptingCore {

    public EventBus bus = new EventBus();
    private HashMap<String, ScriptEngine> engines = new HashMap();
    private ArrayList<ExposedObject> exposedObjects = new ArrayList();
    private File scriptFolder;

    public ScriptingCore() {
    }

    public ScriptEngine getEngine(String engine) {
        return engines.get(engine);
    }

    public void doSetup() {
        this.setupObjects();
        this.setupSupportedEngines();
        this.setupLibraries();
    }

    public Object getVariable_debug(String engine, String var) {
        return engines.get(engine).get(var);
    }

    public Invocable getInvocable(String engine) {
        return (Invocable) engines.get(engine);
    }

    private void setupLibraries() {
        this.runInternalScript("scripts/extract_imports.js");
        this.runInternalScript("scripts/globals.js");
        this.runInternalScript("scripts/globals.lua");
    }

    public void runInternalScript(String path) {
        InputStream st = this.getClass().getClassLoader().getResourceAsStream(path);
        this.importStream(st, path);
    }

    private void setupObjects() {
        exposedObjects.add(new ExposedObject("scriptingCore", this));
        bus.post(new EventExposeObjects(exposedObjects));
        EventSetScriptFolder event = new EventSetScriptFolder();
        bus.post(event);
        this.scriptFolder = event.getFolder();
    }

    private void setupSupportedEngines() {
        for (EnumScripting s : EnumScripting.values()) {
            if (!engines.containsKey(s)) {
                engines.put(s.getEngine(), s.getScriptEngine());
                for (ExposedObject o : exposedObjects) {
                    engines.get(s.getEngine()).put(o.getIdentifier(), o.getObj());
                }
                engines.get(s.getEngine()).put("lang", s.getEngine());
            }
        }
    }

    public void importFile(File file) {
        try {
            FileInputStream f = new FileInputStream(file);
            this.importStream(f, file.getName());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void importStream(InputStream stream, String fileName) {
        for (EnumScripting s : EnumScripting.values()) {
            if (s.isCompatible(fileName)) {
                String script = s.getHandler().Import(stream, this.scriptFolder);
                try {
                    engines.get(s.getEngine()).eval(script);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

}
