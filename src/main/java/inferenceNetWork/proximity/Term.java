package inferenceNetWork.proximity;

import java.io.IOException;

public class Term extends ProximityNode {
    public Term(String term) throws IOException {
        plist=index.get(term);
    }

    @Override
    public void prepare() {

    }
}
