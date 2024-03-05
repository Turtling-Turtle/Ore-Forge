#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_texture;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord0);
    vec4 highlightColor = vec4(0.0, 1.0, 0.0, 1.0); // Green color

    // Calculate the final color by blending the texture color with the highlight color
    vec4 finalColor = mix(texColor, highlightColor, 0.5); // 0.5 is the blending factor, you can adjust it

    gl_FragColor = finalColor * v_color;
}