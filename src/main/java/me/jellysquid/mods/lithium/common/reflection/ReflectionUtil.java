package me.jellysquid.mods.lithium.common.reflection;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.WeakHashMap;

public class ReflectionUtil {

    public static boolean hasMethodOverride(Class<?> clazz, Class<?> superclass, boolean fallbackResult, String methodName, Class<?>... methodArgs) {
        while (clazz != null && clazz != superclass && superclass.isAssignableFrom(clazz)) {
            try {
                clazz.getDeclaredMethod(methodName, methodArgs);
                return true;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            } catch (NoClassDefFoundError error) {
                Logger logger = LogManager.getLogger("Foxium Class Analysis");
                logger.warn("Foxium Class Analysis Error: Class " + clazz.getName() + " cannot be analysed, because" +
                        " getting declared methods crashes with NoClassDefFoundError: " + error.getMessage() +
                        ". This is usually caused by modded" +
                        " entities declaring methods that have a return type or parameter type that is annotated" +
                        " with @Environment(value=EnvType.CLIENT). Loading the type is not possible, because" +
                        " it only exists in the CLIENT environment. The recommended fix is to annotate the method with" +
                        " this argument or return type with the same annotation." +
                        " Foxium handles this error by assuming the class cannot be included in some optimizations.");
                return fallbackResult;
            } catch (Throwable e) {
                final String crashedClass = clazz.getName();
                CrashReport crashReport = CrashReport.forThrowable(e, "Foxium Class Analysis");
                CrashReportCategory crashReportSection = crashReport.addCategory(e.getClass().toString() + " when getting declared methods.");
                crashReportSection.setDetail("Analyzed class", crashedClass);
                crashReportSection.setDetail("Analyzed method name", methodName);
                crashReportSection.setDetail("Analyzed method args", methodArgs);

                throw new ReportedException(crashReport);
            }
        }
        return false;
    }

    //How to find the remapped methods:
    //1) Run in the debugger: System.out.println(FabricLoader.getInstance().getMappingResolver().getNamespaceData("intermediary").methodNames)
    //2) Ctrl+F for the method name, in this case "onEntityCollision". Make sure to find the correct one.
    private static final String REMAPPED_ON_ENTITY_COLLISION = "entityInside";
    private static final WeakHashMap<Class<?>, Boolean> CACHED_IS_ENTITY_TOUCHABLE = new WeakHashMap<>();
    public static boolean isBlockStateEntityTouchable(BlockState operand) {
        Class<? extends Block> blockClazz = operand.getBlock().getClass();
        //Caching results in hashmap as this calculation takes over a second for all blocks together
        Boolean result = CACHED_IS_ENTITY_TOUCHABLE.get(blockClazz);
        if (result != null) {
            return result;
        }
        boolean res = ReflectionUtil.hasMethodOverride(blockClazz, BlockBehaviour.class, true, REMAPPED_ON_ENTITY_COLLISION, BlockState.class, Level.class, BlockPos.class, Entity.class);
        CACHED_IS_ENTITY_TOUCHABLE.put(blockClazz, res);
        return res;
    }
}
