import { create } from "zustand";



interface UseNavStore {
  isVisible: boolean;
  toggle: () => void;
}


export const useNavStore = create<UseNavStore>((set, get) => ({
  isVisible: false,
  toggle: () => {
    const { isVisible } = get();
    set({isVisible: !isVisible});
  }
}));




export default function ViewerContext() {

  const { toggle } = useNavStore();



  return (
    <div className="h-screen" onClick={toggle}>
      <div className="text-center py-20 sm:px-10 xl:px-52">
        <div className="flex justify-end text-left">
          <p className="mr-10 select-none">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum condimentum augue sed felis luctus luctus. Mauris iaculis dolor interdum felis ultrices, egestas aliquam dui sagittis. Vestibulum sed erat pretium, semper mauris id, placerat turpis. In in ultricies nunc, in posuere ante. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Cras et molestie orci. Ut vel blandit mauris. Vestibulum aliquam viverra sem a suscipit. Proin sit amet faucibus orci. Curabitur a massa sapien. Curabitur dolor risus, iaculis sit amet porta non, varius vitae mauris. Ut tincidunt libero quis molestie convallis.
            Phasellus est neque, posuere non consectetur sit amet, euismod aliquet felis. Quisque ornare, lectus ut convallis consequat, lectus ipsum convallis urna, cursus egestas sem urna quis nunc. Ut ut velit sed nisl bibendum rutrum et sed purus. Etiam suscipit lectus et tincidunt feugiat. Praesent placerat at dui ut condimentum. Duis porttitor elit et elementum mollis. Ut vitae mauris lacinia, commodo elit et, vulputate erat. Nullam in felis ac metus facilisis maximus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Duis sed sagittis justo. Maecenas porta justo quis bibendum mattis.
            Maecenas aliquet dignissim condimentum. Maecenas lacinia ultrices justo. In placerat neque quis dui porttitor, mollis pulvinar velit vestibulum. Maecenas bibendum velit non diam suscipit feugiat. Sed rhoncus dolor sed molestie pharetra. Suspendisse ultricies mattis justo, ut accumsan lectus interdum fermentum. Aenean id diam nec augue efficitur fringilla.Donec auctor dui et mi dignissim convallis. Donec mollis varius sem, nec imperdiet mauris molestie et. Fusce rutrum leo et urna cursus finibus. Aenean consequat ante nec 
          </p>
          <p className="ml-10 select-none">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum condimentum augue sed felis luctus luctus. Mauris iaculis dolor interdum felis ultrices, egestas aliquam dui sagittis. Vestibulum sed erat pretium, semper mauris id, placerat turpis. In in ultricies nunc, in posuere ante. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Cras et molestie orci. Ut vel blandit mauris. Vestibulum aliquam viverra sem a suscipit. Proin sit amet faucibus orci. Curabitur a massa sapien. Curabitur dolor risus, iaculis sit amet porta non, varius vitae mauris. Ut tincidunt libero quis molestie convallis.
            Phasellus est neque, posuere non consectetur sit amet, euismod aliquet felis. Quisque ornare, lectus ut convallis consequat, lectus ipsum convallis urna, cursus egestas sem urna quis nunc. Ut ut velit sed nisl bibendum rutrum et sed purus. Etiam suscipit lectus et tincidunt feugiat. Praesent placerat at dui ut condimentum. Duis porttitor elit et elementum mollis. Ut vitae mauris lacinia, commodo elit et, vulputate erat. Nullam in felis ac metus facilisis maximus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Duis sed sagittis justo. Maecenas porta justo quis bibendum mattis.
            Maecenas aliquet dignissim condimentum. Maecenas lacinia ultrices justo. In placerat neque quis dui porttitor, mollis pulvinar velit vestibulum. Maecenas bibendum velit non diam suscipit feugiat. Sed rhoncus dolor sed molestie pharetra. Suspendisse ultricies mattis justo, ut accumsan lectus interdum fermentum. Aenean id diam nec augue efficitur fringilla.Donec auctor dui et mi dignissim convallis. Donec mollis varius sem, nec imperdiet mauris molestie et. Fusce rutrum leo et urna cursus finibus. Aenean consequat ante nec 
          </p>
        </div>
      </div>
    </div>
  );
}
