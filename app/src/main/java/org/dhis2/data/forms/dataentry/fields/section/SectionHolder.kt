package org.dhis2.data.forms.dataentry.fields.section

import android.animation.ValueAnimator
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import io.reactivex.processors.FlowableProcessor
import org.dhis2.Bindings.dp
import org.dhis2.R
import org.dhis2.data.forms.dataentry.fields.FieldViewModel
import org.dhis2.data.forms.dataentry.fields.FormViewHolder
import org.dhis2.databinding.FormSectionBinding
import org.dhis2.utils.customviews.CustomDialog
import org.jetbrains.annotations.NotNull

class SectionHolder(
    binding: @NotNull ViewDataBinding,
    private val selectedSection: @NotNull ObservableField<String>,
    private val sectionProcessor: @NotNull FlowableProcessor<String>
) : FormViewHolder(binding)/*, View.OnClickListener */{

    private lateinit var viewModel: SectionViewModel
    private val formBinding: FormSectionBinding = binding as FormSectionBinding

    init {
        /*selectedSection.addOnPropertyChangedCallback(object : OnPropertyChangedCallback() {
            override fun onPropertyChanged(
                sender: Observable,
                propertyId: Int
            ) {
                setShadows()
                animateArrow()
            }
        })
        formBinding.root.setOnClickListener(this)
        formBinding.descriptionIcon.setOnClickListener {
            showDescription()
        }*/
    }


    public override fun update(viewModel: FieldViewModel) {
        formBinding.sectionView.setViewModel(viewModel as SectionViewModel)
        /*this.viewModel = viewModel as SectionViewModel

        formBinding.descriptionIcon.visibility = GONE
        formBinding.sectionName.viewTreeObserver.addOnGlobalLayoutListener {
            formBinding.sectionName.takeIf { it.text == viewModel.label() }?.layout?.let {
                val isEllipsized = it.getEllipsisCount(0) > 0
                if (viewModel.hasToShowDescriptionIcon(isEllipsized)) {
                    formBinding.descriptionIcon.visibility = VISIBLE
                }
            }
        }

        setShadows()*/
    }

    /*override fun onClick(v: View) {
        if (viewModel.uid() != SectionViewModel.CLOSING_SECTION_UID) {
            sectionProcessor.onNext(viewModel.uid())
        }
    }*/

    /*private fun setShadows() {
        val isSelected = viewModel.isOpen
        if (isSelected) {
            formBinding.shadowTop.visibility = VISIBLE
        } else {
            formBinding.shadowTop.visibility = GONE
        }
    }

    private fun animateArrow() {
        val isSelected = selectedSection.get() == viewModel.uid()
        if (isSelected) {
            formBinding.openIndicator.scaleY = 1f
        }
        formBinding.openIndicator.animate()
            .scaleY(if (isSelected) 1f else -1f)
            .setDuration(200)
            .start()
    }*/

    /*fun setBottomShadow(showShadow: Boolean) {
        formBinding.shadowBottom.visibility = if (showShadow) VISIBLE else GONE
    }*/

    /*fun setLastSectionHeight(previousSectionIsOpened: Boolean) {
        val params = formBinding.lastSectionDetails.layoutParams
        val finalHeight = if (previousSectionIsOpened) {
            48.dp
        } else {
            1.dp
        }
        ValueAnimator.ofInt(params.height, finalHeight).apply {
            duration = 120
            addUpdateListener {
                params.height = it.animatedValue as Int
                formBinding.lastSectionDetails.layoutParams = params
            }
            start()
        }
    }*/

    private fun showDescription() {
        CustomDialog(
            itemView.context,
            viewModel.label(),
            viewModel.description() ?: "",
            itemView.context.getString(R.string.action_close),
            null,
            201,
            null
        ).show()
    }

    fun getSectionView(): SectionView {
        return formBinding.sectionView
    }

    /* fun handleHeaderClick(x: Float) {
         val hasDescription = formBinding.descriptionIcon.visibility == VISIBLE
         val descriptionClicked =
             formBinding.descriptionIcon.x <= x &&
                 formBinding.descriptionIcon.x + formBinding.descriptionIcon.width >= x
         if (hasDescription && descriptionClicked) {
             showDescription()
         } else {
             onClick(itemView)
         }
     }

     fun setSectionNumber(sectionNumber: Int) {
         formBinding.sectionNumber.apply {
             text = sectionNumber.toString()
             background =
                 ContextCompat.getDrawable(itemView.context, R.drawable.ic_circle)
         }
     }*/
}
