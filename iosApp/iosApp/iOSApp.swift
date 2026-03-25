import SwiftUI
import ComposeApp // Aapka KMP module name

// 1. Notification setup ke liye class
class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        UNUserNotificationCenter.currentNotificationCenter().requestAuthorization(options: [.alert, .badge, .sound]) { granted, _ in
            if granted {
                print("Notification Permission Granted ✅")
            }

        }
          BGTaskScheduler.shared.register(forTaskWithIdentifier: "org.yourappdev.homeinterior.checkStatus", using: nil) { task in
                                self.handleBackgroundTask(task: task as! BGAppRefreshTask)
                            }
        return true
    }
    func handleBackgroundTask(task: BGAppRefreshTask) {
            // Kotlin ka UseCase ya Repository yahan call hogi
            // Task khatam hone par task.setTaskCompleted(success: true) lazmi karna hota hai
        }
}

@main
struct iOSApp: App {
    // 2. AppDelegate ko SwiftUI se connect karna
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
init() {
        KoinHelperKt.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            // 3. KMP ka UI yahan load hoga
            ContentView()
                .ignoresSafeArea(.all) // Full screen view ke liye
        }
    }
}