package info.inpureprojects.core.Client;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModMetadata;
import info.inpureprojects.core.API.Scripting.IScriptingCore;
import info.inpureprojects.core.API.Toc.TocManager;
import info.inpureprojects.core.INpureCore;
import info.inpureprojects.core.modInfo;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by den on 7/25/2014.
 */
public class ScriptModContainer extends DummyModContainer {

    private File source;
    private IScriptingCore core;

    public ScriptModContainer(TocManager.TableofContents toc, File source, IScriptingCore core) {
        ModMetadata META = new ModMetadata();
        META.authorList.add(toc.getAuthor());
        META.autogenerated = true;
        META.credits = "";
        META.description = "Fake mod container generated by the INpureCore script engine.";
        META.modId = toc.getTitle();
        META.name = toc.getTitle();
        META.parent = modInfo.modid;
        META.parentMod = FMLCommonHandler.instance().findContainerFor(INpureCore.instance);
        META.version = toc.getVersion();
        try {
            // Stupid private fields.
            Field f = this.getClass().getSuperclass().getDeclaredField("md");
            f.setAccessible(true);
            f.set(this, META);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        this.source = source;
        this.core = core;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        return ResourcePackScript.class;
    }

    @Override
    public File getSource() {
        return this.source;
    }

    public IScriptingCore getCore() {
        return core;
    }
}
