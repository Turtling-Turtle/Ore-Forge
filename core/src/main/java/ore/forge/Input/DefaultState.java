package ore.forge.Input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import ore.forge.ItemMap;
import ore.forge.Screens.InventoryTable;
import ore.forge.Screens.ShopMenu;

import static com.badlogic.gdx.Input.Keys.*;


public class DefaultState extends InputAdapter {
    private final InputManager inputManager;
    private final static ItemMap ITEM_MAP = ItemMap.getSingleton();
    private boolean mouseHeld;
    InventoryTable inventory;
    private ShopMenu shop;
    private final CameraController controller;

    public DefaultState(InputManager inputManager) {
        this.controller = inputManager.getController();
        this.inventory = inputManager.getInventoryTable();
        this.shop = inputManager.getShop();
        this.inputManager = inputManager;

    }

    public void update(OrthographicCamera camera) {
        controller.updateCamera(camera);
//        if (mouseHeld && inputManager.isCoordsValid() && ITEM_MAP.getBlock(inputManager.mouseWorld()) != null) {
        //Transition to Selecting Mode.
//        }
    }

    @Override
    public boolean keyDown(int i) {
        if (inventory.isSearching()) {
//            System.out.println("We are Searching!!");
//            return switch (i) {
//                case ESCAPE -> {
//                    if (inventory.isVisible()) {
//                        System.out.println("Within ESCAPE isVISIble");
//                        if (inventory.isSearching()) {
//                            System.out.println("Stopped Searching!");
//                            inventory.stopSearching();
//                            yield true;
//                        }
//                        inventory.hide();
//                        yield true;
//                    }
//                    yield false;
//                }
//                case F1 -> {
//                    inventory.stopSearching();
//                    inventory.hide();
//                    yield true;
//                }
//                default -> false;
//            };
        }
        return InputManager.handleKey(i, true, controller);
    }

    @Override
    public boolean keyUp(int i) {
//        System.out.println("Called Update!");
        if (inventory.isSearching() || shop.isSearching()) {
//            System.out.println("We are Searching!!");
//            return switch (i) {
//                case ESCAPE -> {
//                    if (inventory.isVisible()) {
//                        System.out.println("Within ESCAPE isVISIble");
//                        if (inventory.isSearching()) {
//                            System.out.println("Stopped Searching!");
//                            inventory.stopSearching();
//                            yield true;
//                        }
//                        inventory.hide();
//                        yield true;
//                    }
//                    yield false;
//                }
//                case F1 -> {
//                    inventory.stopSearching();
//                    inventory.hide();
//                    yield true;
//                }
//                default -> false;
//            };
        }

        boolean returnValue = switch (i) {
            case F1, I -> {
                if (inventory.isVisible()) {
                    inventory.hide();
                } else {
                    inventory.show();
                }
                yield true;
            }
            case F2, B, F -> {
                if (shop.isVisible()) {
                    shop.hide();
                } else {
                    shop.show();
                }
                yield true;
            }
            case F3, J -> {
                //Activate questTab
                yield true;
            }
            case TAB -> {
                if (inventory.isVisible()) {
                    inventory.startSearching();
                }
                yield true;
            }
            case ESCAPE -> {
                if (inventory.isVisible()) {
                    inventory.hide();
                } else {

                }
                yield true;
            }
            default -> false;
        };

        return InputManager.handleKey(i, false, controller) || returnValue;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        if (inventory.isVisible() || shop.isVisible()) {
            return false;
        }
        return switch (i) {
            case LEFT -> {
                mouseHeld = true;
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        if (inventory.isVisible() || shop.isVisible()) {
            return false;
        }
        return switch (i) {
            case LEFT -> {
                mouseHeld = false;
                yield true;
            }
            default -> false;
        };
    }


    public static class Searching extends InputAdapter {
        private final InventoryTable inventory;
        private final ShopMenu shop;
        private final InputMultiplexer inputMultiplexer;

        public Searching(InventoryTable inventoryTable, ShopMenu shop, InputMultiplexer inputMultiplexer) {
            this.inventory = inventoryTable;
            this.shop = shop;
            this.inputMultiplexer = inputMultiplexer;
        }

        @Override
        public boolean keyUp(int i) {
            return switch (i) {
                case ESCAPE -> {
                    inventory.stopSearching();
                    inputMultiplexer.removeProcessor(this);
                    yield true;
                }
                case F1 -> {
                    inventory.stopSearching();
                    inventory.hide();
                    yield true;
                }
                default -> false;
            };
        }
    }


}
