package tw.edu.ntut.sdtlab.crawler.ace.equivalent;

import tw.edu.ntut.sdtlab.crawler.ace.util.Config;

public class EquivalentStateStrategyFactory {
    public EquivalentStateStrategy createStrategy() {
        final String config = Config.EQUIVALENT_LEVEL.toLowerCase();
        if (config.equals("exactlyequivalentstrategy"))
            return new ExactlyEquivalentStrategy();
        else if (config.equals("componentequivalentstrategy"))
            return new CComponentEquivalentStrategy();
        else if(config.equals("activityequivalentstrategy"))
            return new CompositeActivityEquivalentStrategy();
        else
            return null;
    }
}
