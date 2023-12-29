package com.maguro.recipes.data.repository

import com.maguro.recipes.data.local.model.LocalCountry
import com.maguro.recipes.data.local.model.LocalRecipe
import com.maguro.recipes.data.local.model.LocalRecipeDetails
import com.maguro.recipes.data.model.Coordinates
import com.maguro.recipes.data.model.Recipe
import com.maguro.recipes.data.remote.RecipesApi
import com.maguro.recipes.data.remote.model.RemoteBoundingBox
import com.maguro.recipes.data.remote.model.RemoteCountry
import com.maguro.recipes.data.remote.model.RemoteRecipe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class RecipeRepositoryImplTest {

    private lateinit var recipesApi: RecipesApi
    private lateinit var recipesDao: FakeRecipesDao

    private lateinit var repository: RecipeRepositoryImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun before() {
        recipesApi = mockk()
        recipesDao = FakeRecipesDao()

        repository = RecipeRepositoryImpl(
            recipesApi = recipesApi,
            recipesDao = recipesDao,
            ioDispatcher = UnconfinedTestDispatcher()
        )
    }

    val apiResult = listOf(
        RemoteRecipe(
            id = "1",
            name = "Chimichangas",
            imageUrl = "1",
            country = RemoteCountry(
                name = "Mexico",
                code = "MX",
                location = Coordinates(
                    latitude = 10.0,
                    longitude = 20.0
                ),
                boundingBox = RemoteBoundingBox(
                    northWest = Coordinates(1.0, 2.0),
                    southEast = Coordinates(3.0, 4.0)
                )
            ),
            ingredients = listOf(
                "Ingredient 1",
                "Ingredient 2",
                "Ingredient 3"
            ),
            instructions = listOf(
                "Instruction 1",
                "Instruction 2",
                "Instruction 3"
            )
        )
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'all' fetches data from server when local is empty`() = runTest {
        val expected = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }

        coEvery { recipesApi.fetchAll() } returns apiResult

        val emitted = mutableListOf<RequestResult<List<Recipe>>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .all
                .toList(emitted)
        }

        coVerify(exactly = 1) { recipesApi.fetchAll() }

        emitted.size shouldBeEqualTo 2
        emitted[0] shouldBeInstanceOf RequestResult.FirstLoad::class
        emitted[1] shouldBeInstanceOf RequestResult.WithData.Loaded::class
        val loaded = (emitted[1] as RequestResult.WithData.Loaded)
        loaded.data shouldBeEqualTo expected
        loaded.error shouldBeInstanceOf ErrorType.None::class
        recipesDao.stored shouldBeEqualTo expected

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'all' when there is local data stored, return it without fetching`() = runTest {
        val expected = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }

        recipesDao.stored.addAll(expected)
        coEvery { recipesApi.fetchAll() } returns apiResult

        val emitted = mutableListOf<RequestResult<List<Recipe>>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .all
                .toList(emitted)
        }

        coVerify(exactly = 0) { recipesApi.fetchAll() }

        emitted.size shouldBeEqualTo 1
        emitted[0] shouldBeInstanceOf RequestResult.WithData.Loaded::class
        (emitted[0] as RequestResult.WithData.Loaded).data shouldBeEqualTo expected
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'all' when error occurred on first fetch, emit empty list with error`() = runTest {

        coEvery { recipesApi.fetchAll() } throws UnknownHostException()

        val emitted = mutableListOf<RequestResult<List<Recipe>>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .all
                .toList(emitted)
        }

        coVerify(exactly = 1) { recipesApi.fetchAll() }

        emitted.size shouldBeEqualTo 2
        emitted[0] shouldBeInstanceOf RequestResult.FirstLoad::class
        emitted[1] shouldBeInstanceOf RequestResult.WithData.Loaded::class
        val result = (emitted[1] as RequestResult.WithData.Loaded)
        result.data shouldBeEqualTo emptyList()
        result.error shouldBeInstanceOf ErrorType.Connection::class
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'all' refreshes correctly`() = runTest {

        val localData = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }

        val expected = localData.map{
            it.copy(details = it.details.copy(name = "Tacos"))
        }

        recipesDao.stored.addAll(localData)
        coEvery { recipesApi.fetchAll() } returns apiResult.map {
            it.copy(name = "Tacos")
        }

        val emitted = mutableListOf<RequestResult<List<Recipe>>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .all
                .toList(emitted)
        }

        emitted[0] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        repository.reload()

        emitted[1] shouldBeInstanceOf RequestResult.WithData.Refresh::class
        emitted[2] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        coVerify(exactly = 1) { recipesApi.fetchAll() }

        val result = (emitted[2] as RequestResult.WithData.Loaded)
        result.data shouldBeEqualTo expected
        result.error shouldBeInstanceOf ErrorType.None::class
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'all' refresh and error occurred and there is local data, emit local data with error`() = runTest {

        val expected = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }

        recipesDao.stored.addAll(expected)
        coEvery { recipesApi.fetchAll() } throws UnknownHostException()

        val emitted = mutableListOf<RequestResult<List<Recipe>>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .all
                .toList(emitted)
        }

        emitted[0] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        repository.reload()

        emitted[1] shouldBeInstanceOf RequestResult.WithData.Refresh::class
        emitted[2] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        coVerify(exactly = 1) { recipesApi.fetchAll() }

        val result = (emitted[2] as RequestResult.WithData.Loaded)
        result.data shouldBeEqualTo expected
        result.error shouldBeInstanceOf ErrorType.Connection::class
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'getById' fetches data from server when local is empty`() = runTest {
        val expected = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }.first()

        coEvery { recipesApi.fetchById(expected.id) } returns apiResult.first()

        val emitted = mutableListOf<RequestResult<Recipe?>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .getById(expected.id)
                .toList(emitted)
        }

        coVerify(exactly = 1) { recipesApi.fetchById(expected.id) }

        emitted.size shouldBeEqualTo 2
        emitted[0] shouldBeInstanceOf RequestResult.FirstLoad::class
        emitted[1] shouldBeInstanceOf RequestResult.WithData.Loaded::class
        val loaded = (emitted[1] as RequestResult.WithData.Loaded)
        loaded.data shouldBeEqualTo expected
        loaded.error shouldBeInstanceOf ErrorType.None::class
        recipesDao.stored shouldBeEqualTo listOf(expected)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'getById' when there is local data stored, return it without fetching`() = runTest {
        val expected = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }.first()

        recipesDao.stored.add(expected)
        coEvery { recipesApi.fetchById(expected.id) } returns apiResult.first()

        val emitted = mutableListOf<RequestResult<Recipe?>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .getById(expected.id)
                .toList(emitted)
        }

        coVerify(exactly = 0) { recipesApi.fetchById(expected.id) }

        emitted.size shouldBeEqualTo 1
        emitted[0] shouldBeInstanceOf RequestResult.WithData.Loaded::class
        (emitted[0] as RequestResult.WithData.Loaded).data shouldBeEqualTo expected
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'getById' when error occurred on first fetch, emit null with error`() = runTest {

        coEvery { recipesApi.fetchById(any()) } throws UnknownHostException()

        val emitted = mutableListOf<RequestResult<Recipe?>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .getById("")
                .toList(emitted)
        }

        coVerify(exactly = 1) { recipesApi.fetchById(any()) }

        emitted.size shouldBeEqualTo 2
        emitted[0] shouldBeInstanceOf RequestResult.FirstLoad::class
        emitted[1] shouldBeInstanceOf RequestResult.WithData.Loaded::class
        val result = (emitted[1] as RequestResult.WithData.Loaded)
        result.data shouldBeEqualTo null
        result.error shouldBeInstanceOf ErrorType.Connection::class
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'getById' refreshes correctly`() = runTest {

        val localData = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }.first()

        val expected = localData.copy(details = localData.details.copy(name = "Tacos"))

        recipesDao.stored.add(localData)
        coEvery { recipesApi.fetchById(expected.id) } returns apiResult.first().copy(name = "Tacos")

        val emitted = mutableListOf<RequestResult<Recipe?>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .getById(expected.id)
                .toList(emitted)
        }

        emitted[0] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        repository.reloadWithId(expected.id)

        emitted[1] shouldBeInstanceOf RequestResult.WithData.Refresh::class
        emitted[2] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        coVerify(exactly = 1) { recipesApi.fetchById(expected.id) }

        val result = (emitted[2] as RequestResult.WithData.Loaded)
        result.data shouldBeEqualTo expected
        result.error shouldBeInstanceOf ErrorType.None::class
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Flow 'getById' refresh and error occurred and there is local data, emit local data with error`() = runTest {

        val expected = apiResult.map {
            LocalRecipe(
                details = LocalRecipeDetails(it),
                country = LocalCountry(it.country),
                instructions = it.instructions,
                ingredients = it.ingredients
            )
        }.first()

        recipesDao.stored.add(expected)
        coEvery { recipesApi.fetchById(expected.id) } throws UnknownHostException()

        val emitted = mutableListOf<RequestResult<Recipe?>>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            repository
                .getById(expected.id)
                .toList(emitted)
        }

        emitted[0] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        repository.reloadWithId(expected.id)

        emitted[1] shouldBeInstanceOf RequestResult.WithData.Refresh::class
        emitted[2] shouldBeInstanceOf RequestResult.WithData.Loaded::class

        coVerify(exactly = 1) { recipesApi.fetchById(expected.id) }

        val result = (emitted[2] as RequestResult.WithData.Loaded)
        result.data shouldBeEqualTo expected
        result.error shouldBeInstanceOf ErrorType.Connection::class
    }
}