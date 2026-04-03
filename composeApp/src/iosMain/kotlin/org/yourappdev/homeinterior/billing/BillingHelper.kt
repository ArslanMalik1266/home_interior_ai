package org.yourappdev.homeinterior.billing

actual class BillingHelper actual constructor(
    private val onProductsLoaded: (List<PurchaseProduct>) -> Unit,
    private val onPurchaseComplete: (productId: String, credits: Int) -> Unit
) {
    actual fun startConnection() {}
    actual fun launchPurchase(productId: String): Boolean {
        return false  // ✅
    }
    actual fun disconnect() {}
}