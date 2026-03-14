package org.yourappdev.homeinterior.domain.usecase

import org.yourappdev.homeinterior.domain.model.CreditResponse
import org.yourappdev.homeinterior.domain.repo.CreditsRepository

class AddCreditsUseCase(private val repository: CreditsRepository) {

    suspend operator fun invoke(email: String, amount: Int): Result<CreditResponse> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        return repository.addCredits(email, amount)
    }
}