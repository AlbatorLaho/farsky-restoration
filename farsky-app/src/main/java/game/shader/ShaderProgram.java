package game.shader;

import game.Main;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.newdawn.slick.util.ResourceLoader;

public final class ShaderProgram {
   private int programId = 0;
   private int[] textureUniformLocations;

   public ShaderProgram(String vertShaderFile, String fragShaderFile, String[] textureUniforms) {
      int vertShaderId = this.compileVertexShader("shaders/" + vertShaderFile);
      int fragShaderId = this.compileFragmentShader("shaders/" + fragShaderFile);
      int programId = GL20.glCreateProgram();
      GL20.glAttachShader(programId, vertShaderId);
      GL20.glAttachShader(programId, fragShaderId);
      GL20.glLinkProgram(programId);
      if (Main.isVerbose) {
         System.out.println("* Link Log *");
      }

      printProgramLog(programId);
      GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
      GL20.glValidateProgram(programId);
      if (Main.isVerbose) {
         System.out.println("* Validate Log *");
      }

      printProgramLog(programId);
      GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS);
      this.setupTextureUniforms(programId, textureUniforms);
      this.programId = programId;
   }

   private void setupTextureUniforms(int programId, String[] uniformNames) {
      if (uniformNames != null) {
         GL20.glUseProgram(programId);
         this.textureUniformLocations = new int[uniformNames.length];

         for (int i = 0; i < uniformNames.length; i++) {
            this.textureUniformLocations[i] = GL20.glGetUniformLocation(programId, uniformNames[i]);
            if (this.textureUniformLocations[i] < 0 && Main.isVerbose) {
               System.out.println("Texture Location Warning. value: " + this.textureUniformLocations[i]);
            }
         }
      }
   }

   public final void bind() {
      if (Shaders.activeProgramId != this.programId) {
         Shaders.checkGLError();
         GL20.glUseProgram(this.programId);
         if (this.textureUniformLocations != null) {
            for (int i = 0; i < this.textureUniformLocations.length; i++) {
                GL20.glUniform1i(this.textureUniformLocations[i], i);
            }
         }
      }

      Shaders.activeProgramId = this.programId;
   }

   @Override
   public final String toString() {
      return "" + this.programId;
   }

   private int compileVertexShader(String path) {
      int shaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
      if (shaderId == 0) {
         return 0;
      } else {
         String source = "";

         try {
            DataInputStream stream = new DataInputStream(ResourceLoader.getResourceAsStream(path));

            while (stream.available() != 0) {
               source = source + (char)stream.read();
            }
         } catch (Exception e) {
            if (Main.isVerbose) {
               System.out.println("Fail reading vertex shading code");
            }

            return 0;
         }

         GL20.glShaderSource(shaderId, source);
         GL20.glCompileShader(shaderId);
         if (Main.isVerbose) {
            System.out.println("* VertShader log *");
         }

         printShaderLog(shaderId);
         if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            shaderId = 0;
         }

         return shaderId;
      }
   }

   private int compileFragmentShader(String path) {
      int shaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
      if (shaderId == 0) {
         return 0;
      } else {
         String source = "";

         try {
            DataInputStream stream = new DataInputStream(ResourceLoader.getResourceAsStream(path));

            while (stream.available() != 0) {
               source = source + (char)stream.read();
            }
         } catch (Exception e) {
            if (Main.isVerbose) {
               System.out.println("Fail reading fragment shading code");
            }

            return 0;
         }

         GL20.glShaderSource(shaderId, source);
         GL20.glCompileShader(shaderId);
         if (Main.isVerbose) {
            System.out.println("* FragShader log *");
         }

         printShaderLog(shaderId);
         if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            shaderId = 0;
         }

         return shaderId;
      }
   }

   private static boolean printShaderLog(int shaderId) {
      IntBuffer lenBuf = BufferUtils.createIntBuffer(1);
      GL20.glGetShader(shaderId, GL20.GL_INFO_LOG_LENGTH, lenBuf);
      int logLen = lenBuf.get();
      if (logLen > 1) {
         ByteBuffer logBuf = BufferUtils.createByteBuffer(logLen);
         lenBuf.flip();
         GL20.glGetShaderInfoLog(shaderId, lenBuf, logBuf);
         byte[] logBytes = new byte[logLen];
         logBuf.get(logBytes);
         String log = new String(logBytes);
         if (Main.isVerbose) {
            System.out.println(log);
         }

         return false;
      } else {
         return true;
      }
   }

   private static boolean printProgramLog(int programId) {
      IntBuffer lenBuf = BufferUtils.createIntBuffer(1);
      GL20.glGetProgram(programId, GL20.GL_INFO_LOG_LENGTH, lenBuf);
      int logLen = lenBuf.get();
      if (logLen > 1) {
         ByteBuffer logBuf = BufferUtils.createByteBuffer(logLen);
         lenBuf.flip();
         GL20.glGetProgramInfoLog(programId, lenBuf, logBuf);
         byte[] logBytes = new byte[logLen];
         logBuf.get(logBytes);
         String log = new String(logBytes);
         if (Main.isVerbose) {
            System.out.println(log);
         }

         return false;
      } else {
         return true;
      }
   }
}
