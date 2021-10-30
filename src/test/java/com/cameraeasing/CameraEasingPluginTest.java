package com.cameraeasing;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CameraEasingPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CameraEasingPlugin.class);
		RuneLite.main(args);
	}
}