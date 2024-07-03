attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
//uniform float u_time;

varying vec2 v_texCoords;


void main() {
    // Transform the vertex position
    gl_Position = u_projTrans * a_position;

    // Pass texture coordinates to fragment shader
    v_texCoords = a_texCoord0;
}
