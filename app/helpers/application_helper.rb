module ApplicationHelper
  def locale_to_change
    return "<a href='/?locale=zh-TW'>中文版</a>".html_safe if I18n.locale == "en".intern
    return "<a href='/?locale=en'>English</a>".html_safe if I18n.locale == "zh-TW".intern
    return "<a href='/?locale=zh-TW'>中文版</a>".html_safe if I18n.default_locale == "en".intern
    return "<a href='/?locale=en'>English</a>".html_safe 
  end
end
