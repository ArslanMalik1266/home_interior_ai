package org.yourappdev.homeinterior.data.mapper

import org.yourappdev.homeinterior.data.remote.dto.GenerateRoomRequestDto
import org.yourappdev.homeinterior.data.remote.dto.GenerateRoomResponseDto
import org.yourappdev.homeinterior.domain.model.GenerateRoomRequest
import org.yourappdev.homeinterior.domain.model.GenerateRoomResult

fun GenerateRoomResponseDto.toDomain(): GenerateRoomResult {
    return GenerateRoomResult(
        status = status,
        eta = eta,
        fetchUrl = fetchResult,
        id = id,
        images = output,
        futureLinks = futureLinks
    )
}

fun GenerateRoomRequest.toDto(): GenerateRoomRequestDto {
    return GenerateRoomRequestDto(
        initImage = initImage,
        prompt = prompt,
        strength = strength.toString(),
        negativePrompt = negativePrompt
    )
}