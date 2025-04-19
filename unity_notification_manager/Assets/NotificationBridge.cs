using UnityEngine;
using System;
public class NotificationBridge : MonoBehaviour
{
    // Declare external methods to interface with the native Android plugin
    #if UNITY_ANDROID && !UNITY_EDITOR
    private static AndroidJavaClass unityPlayer;
    private static AndroidJavaObject currentActivity;
    private static AndroidJavaClass pluginClass;
    #endif

    void Start()
    {
        CreateNotificationChannel();
    }

    public void CreateNotificationChannel()
    {
        try 
        {
            using (AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
            {
                AndroidJavaObject currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
                using (AndroidJavaClass pluginClass = new AndroidJavaClass("com.alitazik.unity_notification_manager_plugin.NotificationPlugin"))
                {
                    pluginClass.CallStatic("createNotificationChannel");
                }
            }
        }
        catch (Exception e)
        {
            Debug.LogError("Error creating notification channel: " + e.Message);
        }
    }
    public bool ShowNotification(string title, string message)
    {
        try 
        {
            using (AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
            {
                AndroidJavaObject currentActivity = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
                using (AndroidJavaClass pluginClass = new AndroidJavaClass("com.alitazik.unity_notification_manager_plugin.NotificationPlugin"))
                {
                    return pluginClass.CallStatic<bool>("showNotification", title, message);
                }
            }
        }
        catch (Exception e)
        {
            Debug.LogError("Error showing notification: " + e.Message);
            return false;
        }
    }

    // Example method to trigger notification from a button
    public void OnButtonPressed()
    {
        ShowNotification("Unity Notification", "Button was pressed!");
    }
}