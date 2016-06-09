package view;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import utility.Matrix4;

import java.nio.FloatBuffer;
import java.util.Map;

public class ShaderProgram {

    protected static FloatBuffer buf16Pool;

    public static void unbind() {
        GL20.glUseProgram(0);
    }

    public final int program;
    public final int vertex;
    public final int fragment;
    protected String log;

    public ShaderProgram(String vertexSource, String fragmentSource) {
        this(vertexSource, fragmentSource, null);
    }

    public ShaderProgram(String vertexShader, String fragmentShader, Map<Integer, String> attributes) {
        vertex = compileShader(vertexShader, GL20.GL_VERTEX_SHADER);
        fragment = compileShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);

        program = GL20.glCreateProgram();

        GL20.glAttachShader(program, vertex);
        GL20.glAttachShader(program, fragment);

        if (attributes != null)
            for (Map.Entry<Integer, String> e : attributes.entrySet())
                GL20.glBindAttribLocation(program, e.getKey(), e.getValue());

        GL20.glLinkProgram(program);

        String infoLog = GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH));

        if (infoLog!=null && infoLog.trim().length()!=0)
            log += infoLog;

        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
            System.err.println("Failure in linking program. Error log:\n" + infoLog);

        GL20.glDetachShader(program, vertex);
        GL20.glDetachShader(program, fragment);
        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);
    }

    protected int compileShader(String source, int type) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        String infoLog = GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
        if (infoLog!=null && infoLog.trim().length()!=0)
            log += getName(type) +": "+infoLog + "\n";

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
            System.err.println("Failure in compiling " + getName(type) + ". Error log:\n" + infoLog);

        return shader;
    }

    protected String getName(int shaderType) {
        if (shaderType == GL20.GL_VERTEX_SHADER)
            return "GL_VERTEX_SHADER";
        if (shaderType == GL20.GL_FRAGMENT_SHADER)
            return "GL_FRAGMENT_SHADER";
        else
            return "shader";
    }

    public void use() {
        GL20.glUseProgram(program);
    }

    public void dispose() {
        GL20.glDeleteProgram(program);
    }

    public int getUniformLocation(String str) {
        return GL20.glGetUniformLocation(program, str);
    }

    public void setUniformi(int loc, int i) {
        if (loc==-1) return;
        GL20.glUniform1i(loc, i);
    }

    public void setUniformMatrix(int loc, boolean transposed, Matrix4 mat) {
        if (loc==-1) return;
        if (buf16Pool == null)
            buf16Pool = BufferUtils.createFloatBuffer(16);
        buf16Pool.clear();
        mat.store(buf16Pool);
        buf16Pool.flip();
        GL20.glUniformMatrix4(loc, transposed, buf16Pool);
    }

}