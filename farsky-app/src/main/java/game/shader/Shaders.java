package game.shader;

import game.Main;
import game.util.Point;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class Shaders {
   public static boolean testShaderEnabled = false;
   public static ShaderProgram worldShader;
   public static ShaderProgram worldFloorShader;
   public static ShaderProgram mapShader;
   public static ShaderProgram blurShader;
   public static ShaderProgram brightPassShader;
   public static ShaderProgram bloomAdditiveShader;
   public static ShaderProgram handsShader;
   public static ShaderProgram enemyShader;
   public static ShaderProgram blackBordersShader;
   public static ShaderProgram crackEffectShader;
   public static ShaderProgram insideShader;
   public static ShaderProgram subShader;
   public static ShaderProgram surfaceShader;
   public static ShaderProgram guiEffectShader;
   public static ShaderProgram worldTestShader;
   public static ShaderProgram worldFloorTestShader;
   public static int activeProgramId = -1;

   public static void loadAll() {
      if (Main.isVerbose) {
         System.out.println("****************************");
      }

      if (Main.isVerbose) {
         System.out.println("** Shaders Loading Begins **");
      }

      if (Main.isVerbose) {
         System.out.println("== Loading world Shader ==");
      }

      worldShader = new ShaderProgram("world/world.vert", "world/world.frag", new String[]{"colorTex"});
      if (Main.isVerbose) {
         System.out.println("== Loading worldFloor Shader ==");
      }

      worldFloorShader = new ShaderProgram("worldFloor/worldFloor.vert", "worldFloor/worldFloor.frag", new String[]{"colorTex", "normalTex", "causticTex"});
      if (Main.isVerbose) {
         System.out.println("== Loading map Shader ==");
      }

      mapShader = new ShaderProgram("map/map.vert", "map/map.frag", null);
      if (Main.isVerbose) {
         System.out.println("== Loading blur Shader ==");
      }

      blurShader = new ShaderProgram("blur/blur.vert", "blur/blur.frag", new String[]{"texture"});
      if (Main.isVerbose) {
         System.out.println("== Loading bright-pass Shader ==");
      }

      brightPassShader = new ShaderProgram("bloom/standard.vert", "bloom/bright_pass.frag", new String[]{"texture"});
      if (Main.isVerbose) {
         System.out.println("== Loading bloomAdditive Shader ==");
      }

      bloomAdditiveShader = new ShaderProgram("bloom/standard.vert", "bloom/bloomAdditive.frag", new String[]{"src", "dst"});
      if (Main.isVerbose) {
         System.out.println("== Loading hands Shader ==");
      }

      handsShader = new ShaderProgram("hands/hands.vert", "hands/hands.frag", new String[]{"colorTex"});
      if (Main.isVerbose) {
         System.out.println("== Loading enemy Shader ==");
      }

      enemyShader = new ShaderProgram("enemy/enemy.vert", "enemy/enemy.frag", new String[]{"colorTex"});
      if (Main.isVerbose) {
         System.out.println("== Loading blackBorders Shader ==");
      }

      blackBordersShader = new ShaderProgram("blackBorders/blackBorders.vert", "blackBorders/blackBorders.frag", new String[]{"texture"});
      if (Main.isVerbose) {
         System.out.println("== Loading crackEffect Shader ==");
      }

      crackEffectShader = new ShaderProgram("crackEffect/crackEffect.vert", "crackEffect/crackEffect.frag", new String[]{"screen", "crack"});
      if (Main.isVerbose) {
         System.out.println("== Loading inside Shader ==");
      }

      insideShader = new ShaderProgram("inside/inside.vert", "inside/inside.frag", new String[]{"texture"});
      if (Main.isVerbose) {
         System.out.println("== Loading sub Shader ==");
      }

      subShader = new ShaderProgram("sub/sub.vert", "sub/sub.frag", new String[]{"texture"});
      if (Main.isVerbose) {
         System.out.println("== Loading Surface Shader ==");
      }

      surfaceShader = new ShaderProgram("surface/surface.vert", "surface/surface.frag", null);
      if (Main.isVerbose) {
         System.out.println("== Loading guiEffect Shader ==");
      }

      guiEffectShader = new ShaderProgram("guiEffect/guiEffect.vert", "guiEffect/guiEffect.frag", new String[]{"texture"});
      if (!Main.isRelease) {
         if (Main.isVerbose) {
            System.out.println("== Loading worldTest Shader ==");
         }

         worldTestShader = new ShaderProgram("world/world.vert", "test/worldTest.frag", null);
         if (Main.isVerbose) {
            System.out.println("== Loading worldFloorTest Shader ==");
         }

         worldFloorTestShader = new ShaderProgram("worldFloor/worldFloor.vert", "test/worldTest.frag", null);
      }

      if (Main.isVerbose) {
         System.out.println("****************************");
         System.out.println("******** Shaders Ids *******");
         System.out.println("world:\t" + worldShader);
         System.out.println("worldFloor:\t" + worldFloorShader);
         System.out.println("map:\t" + mapShader);
         System.out.println("blur:\t" + blurShader);
         System.out.println("bright_pass:\t" + brightPassShader);
         System.out.println("bloomAdditive:\t" + bloomAdditiveShader);
         System.out.println("hands:\t" + handsShader);
         System.out.println("enemy:\t" + enemyShader);
         System.out.println("blackBorders:\t" + blackBordersShader);
         System.out.println("inside:\t" + insideShader);
         System.out.println("crackEffect:\t" + crackEffectShader);
         System.out.println("sub:\t" + subShader);
         System.out.println("surface:\t" + surfaceShader);
         System.out.println("guiEffect:\t" + guiEffectShader);
         System.out.println("*** Shaders Loading Ends ***");
         System.out.println("****************************");
      }
   }

   public static void unbind() {
      GL20.glUseProgram(GL11.GL_NONE);
      activeProgramId = GL11.GL_NONE;
   }

   public static void setUniform(String name, Point point) {
      GL20.glUniform3f(GL20.glGetUniformLocation(activeProgramId, name), point.x, point.y, point.z);
      if (checkGLError() && Main.isVerbose) {
         System.out.println("var: " + name + ", current program: " + activeProgramId);
      }

      if (checkGLError() && Main.isVerbose) {
         System.out.println("var: " + name + ", current program: " + activeProgramId);
      }
   }

   public static void setUniform(String name, double value) {
      GL20.glUniform1f(GL20.glGetUniformLocation(activeProgramId, name), (float)value);
      if (checkGLError() && Main.isVerbose) {
         System.out.println("var: " + name + ", current program: " + activeProgramId);
      }
   }

   public static void setUniform(String name, boolean value) {
      int location = GL20.glGetUniformLocation(activeProgramId, name);
      if (!value) {
         GL20.glUniform1i(location, GL11.GL_FALSE);
      }

      if (value) {
         GL20.glUniform1i(location, GL11.GL_TRUE);
      }

      if (checkGLError() && Main.isVerbose) {
         System.out.println("var: " + name + ", current program: " + activeProgramId);
      }
   }

   public static boolean checkGLError() {
      int error = GL11.glGetError();
      boolean hasError = true;
      if (Main.isVerbose) {
         switch (error) {
            case 0:
               hasError = false;
               break;
            case GL11.GL_INVALID_ENUM:
               System.err.println("Error Flag: GL_INVALID_ENUM");
               break;
            case GL11.GL_INVALID_VALUE:
               System.err.println("Error Flag: GL_INVALID_VALUE");
               break;
            case GL11.GL_INVALID_OPERATION:
               System.err.println("Error Flag: GL_INVALID_OPERATION");
               break;
            case GL11.GL_STACK_OVERFLOW:
               System.err.println("Error Flag: GL_STACK_OVERFLOW");
               break;
            case GL11.GL_STACK_UNDERFLOW:
               System.err.println("Error Flag: GL_STACK_UNDERFLOW");
               break;
            case GL11.GL_OUT_OF_MEMORY:
               System.err.println("Error Flag: GL_OUT_OF_MEMORY");
               break;
            default:
               System.err.println("Error Flag: Unknown error -> id=" + error);
         }
      }

      return hasError;
   }
}
