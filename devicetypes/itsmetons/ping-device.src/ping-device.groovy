


metadata {
    definition (name: "ping Device", namespace: "itsmetons", author: "Ton Rios", ocfDeviceType: "oic.d.tv") {
		capability "switch"
		capability "refresh"
      
    }

	tiles(scale: 2) {

        standardTile("switch", "device.switch", width: 6, height: 6, canChangeIcon: true) {
    		state "off", label: 'Offline', icon: "st.Electronics.electronics18", backgroundColor: "#ff0000", nextState: "on" 
    		state "on", label: 'Online', icon: "st.Electronics.electronics18", backgroundColor: "#79b821", nextState: "off" 
		}
		
		 standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}
        
    	main("switch")
        details(["switch"])

	    preferences {
		    section("Device Settings:") {
			    input "ip_device", "string", title:"Device to Ping", description: "Host or IP Address", required: true, displayDuringSetup: true
		    }
	    }
    }
}

def initialize() {
	 ping()
}

def installed() {
    ping()
}

def updated() {	
    ping()
}

def refresh() {
	 log.debug "refesh"
 	ping()
}


def on() {
    sendEvent(name: "switch", value: "on");
    log.debug "Online"
}

def off() {
    sendEvent(name: "switch", value: "off");
    log.debug "Offline"
}

def ping() {

	log.debug "Ataulizaando"
	unschedule(ping)
    unschedule(pingTimeout)
	
	def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/status",
        headers: ["HOST" : "${ip_device}:80", "Content-Type": "application/json" ],
        null,
        [callback: pingCallback]
	)
    log.debug result
    sendHubCommand(result)	
    runIn(5,ping)
    runIn(4, pingTimeout)
}

def pingCallback(physicalgraph.device.HubResponse hubResponse) {
    unschedule(pingTimeout)
  	sendEvent(name: "switch", value: "on");
    log.debug "Online"
}

def pingTimeout() {
	sendEvent(name: "switch", value: "off")
    log.debug "Offline"    
}