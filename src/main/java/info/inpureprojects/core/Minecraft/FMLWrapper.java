package info.inpureprojects.core.Minecraft;

import cpw.mods.fml.common.registry.LanguageRegistry;
import info.inpureprojects.core.INpureCore;
import info.inpureprojects.core.Item.ItemScriptable;
import info.inpureprojects.core.Scripting.Dynamic.DynamicFactory;
import info.inpureprojects.core.Scripting.Dynamic.IFML;
import info.inpureprojects.core.Scripting.Dynamic.IMinecraft;
import info.inpureprojects.core.Scripting.Dynamic.IScriptableItem;
import info.inpureprojects.core.Scripting.ScriptingCore;
import net.minecraft.item.Item;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by den on 7/19/2014.
 */
public class FMLWrapper {

    public Item registerItem(ScriptingCore core, String engine, Object o) {
        IScriptableItem proxy = (IScriptableItem) DynamicFactory.instance.create(core, engine, o, IScriptableItem.class);
        ItemScriptable i = new ItemScriptable(proxy);
        Item.itemRegistry.addObject(5000, proxy.getUnlocalizedName(), i);
        return i;
    }

    public void loadJar(String path) {
        INpureCore.proxy.loadJar(new File(path));
    }

    public void registerModLoadEvents(ScriptingCore core, String engine, Object o) {
        Object proxy = DynamicFactory.instance.create(core, engine, o, IFML.class);
        core.forwardingBus.register(proxy);
    }

    public void registerMinecraftEvents(ScriptingCore core, String engine, Object o) {
        Object proxy = DynamicFactory.instance.create(core, engine, o, IMinecraft.class);
        core.forwardingBus.register(proxy);
    }

    public String translate(String key) {
        return LanguageRegistry.instance().getStringLocalization(key);
    }

    public void registerLangFile(File workspace, String path) {
        if (!path.contains(".lang")) {
            INpureCore.proxy.warning("Lang file has the wrong extension! " + path);
        }
        File lang = new File(workspace, path);
        if (!lang.exists()) {
            INpureCore.proxy.severe("Lang file does not exist! " + lang.getAbsolutePath());
        } else {
            String key = FilenameUtils.removeExtension(lang.getName());
            INpureCore.proxy.print("Parsing lang file " + path);
            try {
                FileInputStream in = new FileInputStream(lang);
                this.Import(key, in);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void Import(String key, InputStream in) {
        try {
            List<String> lines = IOUtils.readLines(in);
            HashMap<String, String> map = new HashMap();
            for (String s : lines) {
                String[] parse = s.split("=");
                map.put(parse[0], parse[1]);
            }
            LanguageRegistry.instance().injectLanguage(key, map);
            in.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}