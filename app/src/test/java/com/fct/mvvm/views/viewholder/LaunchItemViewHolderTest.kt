package com.fct.mvvm.views.viewholder

import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fct.mvvm.TestApplication
import com.fct.mvvm.createLaunchEntity
import com.fct.mvvm.data.LaunchEntity
import com.fct.mvvm.databinding.LaunchListItemBinding
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class LaunchItemViewHolderTest {

    private val mockContext: Context = ApplicationProvider.getApplicationContext()
    private lateinit var viewHolder: LaunchItemViewHolder

    @MockK
    private lateinit var mockClickListener: (entity: LaunchEntity) -> Unit

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { mockClickListener.invoke(any()) } returns Unit

        val inflater = LayoutInflater.from(mockContext)
        val binding = LaunchListItemBinding.inflate(inflater, ConstraintLayout(mockContext), false)
        viewHolder = LaunchItemViewHolder(binding, mockClickListener)
    }

    @Test
    fun `ensure LaunchItemViewHolder binds data properly`() {
        // arrange
        val entity = createLaunchEntity()

        // act
        viewHolder.bind(entity)

        // assert
        with(viewHolder.binding) {
            assertEquals(entity.name, txtName.text)
            assertEquals(entity.flickrImages?.size.toString(), txtPhotoCount.text)
            assertEquals("Successful", txtSuccess.text)
            assertEquals("01/31/2021", txtDate.text)
        }
    }

    @Test
    fun `ensure cell onclick works properly for LaunchItemViewHolder`() {
        // arrange
        val entity = createLaunchEntity()

        // act
        viewHolder.bind(entity)
        viewHolder.itemView.performClick()

        // assert
        verify { mockClickListener.invoke(entity) }
    }
}