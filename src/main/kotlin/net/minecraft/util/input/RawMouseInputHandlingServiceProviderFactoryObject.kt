package net.minecraft.util.input

/**
 *
 * this supposed to detect mouse and get raw input from it.
 * (no work because I don't know how to implement this correctly.)
 *
 */

/*import kotlinx.coroutines.*
import net.java.games.input.Controller
import net.java.games.input.ControllerEnvironment
import net.java.games.input.Mouse
import net.minecraft.client.Minecraft
import java.lang.reflect.Constructor

class RawMouseInputHandlingServiceProviderFactoryObject {
    private val mouseInputHelper = MouseInputHelper()
    private var deltaX = mouseInputHelper.deltaX
    private var deltaY = mouseInputHelper.deltaY

    // Completely null safe. Provided by Spoili. (the most skillful Kotlin Multiplatform developer)
    private fun createDefaultEnvironment(): ControllerEnvironment? {
        return try {
            val constructor = Class.forName("net.java.games.input.DefaultControllerEnvironment")
                .declaredConstructors[0]
            constructor.takeIf { it.parameterCount == 0 }?.let {
                it.isAccessible = true
                it.newInstance() as? ControllerEnvironment
            }
        } catch (e: ReflectiveOperationException) {
            null
        }
    }

    /**
     *
     * Try to find mouse if mouse is not defined already.
     *
     */
    fun indexMouse() = runBlocking {
        // Minecraft.getMinecraft().mouseHelper = RawMouseInputHandlingServiceProviderFactoryObject()

        launch(Dispatchers.Default) {
            var enviro: ControllerEnvironment? = null
            var mouse: Mouse? = null

            while (isActive) {
                if (enviro == null) {
                    enviro = createDefaultEnvironment()
                } else if (mouse == null) {
                    enviro.controllers?.forEach { controller ->
                        if (controller.type == Controller.Type.MOUSE) {
                            controller.poll()
                            val px = (controller as? Mouse)?.x?.pollData ?: 0f
                            val py = (controller as? Mouse)?.y?.pollData ?: 0f
                            val eps = 0.1f

                            // check if mouse is moving
                            if (px < -eps || px > eps || py < -eps || py > eps) {
                                mouse = controller as? Mouse
                            }
                        }
                    }
                } else {
                    mouse?.poll()
                    if (Minecraft.getMinecraft().currentScreen == null) {
                        deltaX += mouse?.x?.pollData?.toInt() ?: 0
                        deltaY += mouse?.y?.pollData?.toInt() ?: 0
                    }
                }

                delay(1)
            }
        }
    }
}
*/
