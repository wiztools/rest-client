package org.wiztools.restclient.ui.resstats;

import com.google.inject.ImplementedBy;
import org.wiztools.restclient.ui.ViewPanel;

/**
 *
 * @author subhash
 */
@ImplementedBy(ResStatsPanelImpl.class)
public interface ResStatsPanel extends ViewPanel {
    long getExecutionTime();
    long getBodySize();
    
    void setExecutionTime(long time);
    void setBodySize(long size);
}
