#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform vec2 u_resolution;
uniform float u_time;
uniform sampler2D u_texture;

void main() {
    // Normalized pixel coordinates (0.0 to 1.0)
    vec4 textureColor = texture2D(u_texture, v_texCoords);
    vec2 uv = gl_FragCoord.xy / u_resolution.xy;

    // Simple fire effect
    float strength = 1.0 - abs(sin(u_time * 0.1 + uv.y * 5.0));

    // Color based on intensity
    vec3 color = vec3(1.0, 0.3, 0.1) * strength;

    // Output to screen
    gl_FragColor = textureColor * vec4(color, 1.0);
}
