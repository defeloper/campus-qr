package app

import Url
import com.studo.campusqr.common.UserData
import com.studo.campusqr.common.UserType
import com.studo.campusqr.common.utils.LocalizedString
import kotlinext.js.js
import kotlinx.html.js.onClickFunction
import org.w3c.dom.events.Event
import react.*
import react.dom.a
import react.dom.div
import react.dom.jsStyle
import util.AppRoute
import util.get
import util.localizedStringAction
import views.common.spacer
import views.settings.renderSettings
import webcore.materialUI.*
import webcore.ui.logoBadge

class SideDrawerItem(
  val label: LocalizedString,
  val url: Url,
  val icon: RClass<IconProps>
)

interface AppDrawerItemsProps : RProps {
  class Config(
    val userData: UserData?,
    val currentAppRoute: AppRoute?,
    val loading: Boolean,
    val accessManagerSideDrawerItems: List<SideDrawerItem>,
    val moderatorSideDrawerItems: List<SideDrawerItem>,
    val adminSideDrawerItems: List<SideDrawerItem>,
    val onCloseMobileNav: () -> Unit
  )

  var config: Config
  var classes: AppDrawerClasses
}

interface AppDrawerItemsState : RState

class AppDrawerItems : RComponent<AppDrawerItemsProps, AppDrawerItemsState>() {
  override fun RBuilder.render() {
    logoBadge(
      logoUrl = "$baseUrl/static/images/logo_campusqr.png",
      logoAlt = "UniQR",
      badgeTitle = "UniQR",
      badgeSubtitle = props.config.userData?.clientUser?.name ?: ""
    )

    if (props.config.loading) {
      linearProgress {}
    }

    fun drawerListItem(
        label: String,
        url: String? = null,
        onClick: ((event: Event) -> Unit)? = null,
        icon: RClass<IconProps>? = null,
        selected: Boolean = false,
        openInNewTab: Boolean = false,
        showLoadingIndicator: Boolean = false
    ) {
      if (url != null && onClick != null) {
        throw IllegalArgumentException("Either specify url or onClick")
      }

      a(classes = props.classes.drawerLink) {
        url?.let { url -> attrs.href = url }
        onClick?.let { onClick ->
          attrs.onClickFunction = { event ->
            onClick(event)
            props.config.onCloseMobileNav()
          }
        }
        if (openInNewTab) {
          attrs.target = "_blank"
        }
        attrs.rel = "noopener"
        listItem {
          attrs.selected = selected
          attrs.button = true
          listItemIcon {
            if (showLoadingIndicator) {
              circularProgress { attrs.size = "25px" }
            } else {
              icon?.invoke { }
            }
          }
          listItemText {
            attrs.classes = js {
              primary = props.classes.drawerItemText
            }
            +label
                .replace("_", "_\u00ad") // Add soft hyphen after some gender-gap characters for long gendered words
                .replace("*", "*\u00ad")
                .replace("/", "/\u00ad")
                .replace(":", ":\u00ad")
          }
        }
      }
    }

    fun drawerItems() {
      val userType = props.config.userData?.clientUser?.type?.let { UserType.valueOf(it) }

      if (userType == UserType.ACCESS_MANAGER || userType == UserType.MODERATOR || userType == UserType.ADMIN) {
        listSubheader {
          +UserType.ACCESS_MANAGER.localizedStringAction.get()
        }
        props.config.accessManagerSideDrawerItems.forEach { sideDrawerItem ->
          drawerListItem(
            label = sideDrawerItem.label.get(),
            icon = sideDrawerItem.icon,
            selected = props.config.currentAppRoute?.url == sideDrawerItem.url,
            url = sideDrawerItem.url.path
          )
        }
      }

      if (userType == UserType.MODERATOR || userType == UserType.ADMIN) {
        divider {}
        listSubheader {
          +UserType.MODERATOR.localizedStringAction.get()
        }
        props.config.moderatorSideDrawerItems.forEach { sideDrawerItem ->
          drawerListItem(
            label = sideDrawerItem.label.get(),
            icon = sideDrawerItem.icon,
            selected = props.config.currentAppRoute?.url == sideDrawerItem.url,
            url = sideDrawerItem.url.path
          )
        }
      }

      if (userType == UserType.ADMIN) {
        divider {}
        listSubheader {
          +UserType.ADMIN.localizedStringAction.get()
        }
        props.config.adminSideDrawerItems.forEach { sideDrawerItem ->
          drawerListItem(
            label = sideDrawerItem.label.get(),
            icon = sideDrawerItem.icon,
            selected = props.config.currentAppRoute?.url == sideDrawerItem.url,
            url = sideDrawerItem.url.path
          )
        }
      }
    }

    list {
      attrs.disablePadding = true

      // Student specific features
      drawerItems()
    }

    div {
      attrs.jsStyle {
        flex = "1"
      }
    }
    renderSettings(userData = props.config.userData)
    spacer(16)
  }
}

interface AppDrawerClasses {
  val drawerLink: String
  val drawerItemText: String
  val footerImage: String
}

private val AppDrawerStyle = { theme: dynamic ->
  js {
    drawerLink = js {
      textDecoration = "none!important"
      color = ColorPalette.textDefault
    }
    drawerItemText = js {
      fontSize = "14px"
      hyphens = "auto"
    }
    footerImage = js {
      width = "60%"
      marginLeft = "auto"
      marginRight = "auto"
      marginBottom = "20px"
      marginTop = "20px"
    }
  }
}

private val styled = withStyles<AppDrawerItemsProps, AppDrawerItems>(AppDrawerStyle)

fun RBuilder.renderAppDrawerItems(config: AppDrawerItemsProps.Config) = styled {
  attrs.config = config
}