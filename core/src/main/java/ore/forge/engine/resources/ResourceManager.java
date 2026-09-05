package ore.forge.engine.resources;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.engine.Handle;
import ore.forge.engine.definitions.AssetType;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Public resource-system entry point for importing, registry persistence, CPU residency, and GPU residency.
 */
public class ResourceManager {
    private final AssetRegistry registry;
    private final AssetImporter importer;
    private final AssetManager assetManager;
    private final GpuResourceManager gpuResourceManager;
    
    enum RequestType {
        SYNCHRONOUS,
        ASYNC_IMMEDIATE,
        ASYNC_CALLBACK
    }

    public ResourceManager() {
        this(new AssetRegistry());
    }

    public ResourceManager(String bakedOutputDir) {
        this(new AssetRegistry(bakedOutputDir));
    }

    private ResourceManager(AssetRegistry registry) {
        this.registry = registry;
        this.importer = new AssetImporter(registry);
        this.assetManager = new AssetManager(registry);
        this.gpuResourceManager = new GpuResourceManager(assetManager);
    }

    public void importGltf(Path file) {
        importer.importGlbFile(file);
    }

    public CpuAssetData getCpuAsset(Handle<CpuAssetData> handle) {
        return assetManager.resolveHandle(handle);
    }

    public GpuResource getGpuResource(Handle<GpuResource> assetHandle) {
        return gpuResourceManager.resolveHandle(assetHandle);
    }

    public AssetType getAssetType(AssetID id) {
        return registry.requireArtifact(id).sourceKey().assetType();
    }

    public Iterable<AssetID> getAssetIDs() {
        return registry.getIDs();
    }

    public void saveRegistry(Path outputFile) {
        registry.save(outputFile.toFile());
    }

    public void loadRegistry(JsonValue jsonValue) {
        registry.load(jsonValue);
    }

    public void loadRegistry(FileHandle fileHandle) {
        loadRegistry(new JsonReader().parse(fileHandle));
    }

    public void releaseGpuResource(Handle<GpuResource> handle) {
        gpuResourceManager.releaseHandle(handle);
    }

    public int activeCpuResources() {
        //TODO
        return -1;
    }

    public int activeGpuResources() {
        return gpuResourceManager.resouceCount();
    }

    //---synchronous loading---   
    public Handle<CpuAssetData> acquireCpuDataSync(AssetID id) {
        return assetManager.acquireHandle(id, RequestType.SYNCHRONOUS);
    }

    public Handle<GpuResource> acquireGpuResourceSync(AssetID id) {
        return gpuResourceManager.accquireHandle(id, RequestType.SYNCHRONOUS);
    }

    public Handle<CpuAssetData> acquireCpuDataAsync(AssetID id) {
        return assetManager.acquireHandle(id, RequestType.ASYNC_IMMEDIATE);
    }

    public Handle<GpuResource> acquireGpuResourceAsync(AssetID id) {
        return gpuResourceManager.accquireHandle(id, RequestType.ASYNC_IMMEDIATE);
    }

    public CompletableFuture<Handle<CpuAssetData>> acquireCpuDataThen(AssetID id) {
        return assetManager.asyncCallback(id);
    }

    public CompletableFuture<Handle<GpuResource>> acqurieGpuResourceThen(AssetID id) {
        return gpuResourceManager.asyncCallback(id);
    }

    //------------- Batching Api --------------------------
    public Iterable<Handle<CpuAssetData>> acquireCpuDataSync(Iterable<AssetID> ids) {
        Array<Handle<CpuAssetData>> handles = new Array<>();
        for (AssetID id : ids) {
            handles.add(assetManager.acquireHandle(id, RequestType.SYNCHRONOUS));
        }
        return handles;    
    }

    public Iterable<Handle<GpuResource>> acquireGpuResourcesSync(Iterable<AssetID> ids) {
        Array<Handle<GpuResource>> handles = new Array<>();
        for (AssetID id : ids) {
            handles.add(gpuResourceManager.accquireHandle(id, RequestType.SYNCHRONOUS));
        }
        return handles;
    }

}
