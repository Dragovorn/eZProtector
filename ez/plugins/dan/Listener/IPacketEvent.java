/*
Copyright (c) 2016 dvargas135

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package ez.plugins.dan.Listener;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import ez.plugins.dan.Main;

public class IPacketEvent {
	private static FileConfiguration config = Main.getPlugin().getConfig();
	
	public static void protocolLibHook(final List<String> list) {
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter
			(Main.instance, ListenerPriority.HIGHEST, new PacketType[] { 
		PacketType.Play.Client.TAB_COMPLETE }) {
			public void onPacketReceiving(PacketEvent e) {
				String prefix = config.getString("prefix").replaceAll("&", "§");
				Player p = e.getPlayer();
				
				if (e.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
					PacketContainer packet = e.getPacket();
					String message = ((String)packet.getSpecificModifier(String.class).read(0)).toLowerCase();
					if (!e.getPlayer().hasPermission("ezprotector.bypass.tabcompletion")) {
						for (String command : list) {
							if (message.startsWith(command)) {
								if (config.getBoolean("tab-completion.notify-admins")) {
									for (Player admins : Main.getPlugin().getServer().getOnlinePlayers()) {
										if (admins.hasPermission("ezprotector.notify.tabcompletion")) {
											admins.sendMessage(prefix + config.getString("tab-completion.notify-admins.message")
		    								.replaceAll("&", "§").replaceAll("%player%", p.getName())
		    									.replaceAll("%message%", "'" + message + "<tab>'"));
										}
									}
								}
								if (config.getBoolean("tab-completion.warn-player.enabled")) {
									p.sendMessage(config.getString("tab-completion.message")
										.replaceAll("&", "§").replaceAll("%player%", p.getName())
	    									.replaceAll("%message%", "'" + message + "<tab>'"));
								}
							}
							if ((message.startsWith(command)) || ((message.startsWith("/")) && ((!message.contains(" ")) || 
									(message.contains(":"))))) {
								e.setCancelled(true);
							}
						} 
					}
				}
			}
		});
	}
}
