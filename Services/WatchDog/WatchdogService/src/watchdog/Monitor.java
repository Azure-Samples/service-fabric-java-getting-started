// ------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
//  Licensed under the MIT License (MIT). See License.txt in the repo root for license information.
// ------------------------------------------------------------

package watchdog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.fabric.FabricClient;
import system.fabric.HealthClient;
import system.fabric.health.HealthInformation;
import system.fabric.health.HealthState;
import system.fabric.health.ServiceHealthReport;

public class Monitor {

    private static final Logger logger = Logger.getLogger(Monitor.class.getName());
    private FabricClient fabricClient = null;
    private HealthClient healthClient = null;
    public Monitor() { }

    public void reportHealth(HttpCommunicationClient client) {
        if (this.fabricClient == null || this.healthClient == null) {
            this.fabricClient = new FabricClient();
            this.healthClient = this.fabricClient.getHealthManager();
        }

        if (this.fabricClient == null || this.healthClient == null) {
            logger.log(Level.WARNING, "FabricClient or HealthClient is null");
            return;
        }

        try {
            String address = client.endPointAddress();
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            rd.close();
            String str = result.toString();
            String[] values = str.split("::", 0);
            String version = values[0].replaceAll("\\<.*?> *", "").trim();
            Boolean isError = false;
            if (!version.equals("[Version 1.0]") && !version.equals("[Version 2.0]")) {
                isError = true;
            }

            if (!isError) {
                SendHealthEvent(HealthState.Ok, client);
            } else {
                SendHealthEvent(HealthState.Error, client);
            }

        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception in Monitor:{0}", ex.toString());
            SendHealthEvent(HealthState.Error, client);
            throw new RuntimeException(ex);
        }
    }

    private void SendHealthEvent(HealthState state, HttpCommunicationClient client) {
        HealthInformation info = new HealthInformation("Watchdog", "Property", state);
        info.setDescription("Health reported by WatchDog service");
        info.setRemoveWhenExpired(true);
        info.setTimeToLiveSeconds(Duration.ofMinutes(5));
        ServiceHealthReport report = new ServiceHealthReport(client.resolvedServicePartition().getServiceName(), info);
        this.healthClient.reportHealth(report);
    }
}
